using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;

#if WINDOWS_PHONE
using Microsoft.Phone.Reactive;
using Microsoft.Phone.Info;
#else
using System.ComponentModel;
using System.Runtime.InteropServices.WindowsRuntime;
using System.Threading.Tasks;
using Windows.Foundation;
using Windows.System.Threading;
#endif

namespace TapstreamMetrics.Sdk
{
	class Core
	{
		public const string VERSION = "2.3";
		private const string EVENT_URL_TEMPLATE = "https://api.tapstream.com/{0}/event/{1}/";
		private const string HIT_URL_TEMPLATE = "http://api.tapstream.com/{0}/hit/{1}.gif";
        private const string CONVERSION_URL_TEMPLATE = "http://reporting.tapstream.com/v1/conversions/lookup?secret={0}&event_session={1}";
		private const int MAX_THREADS = 1;
        private const int CONVERSION_POLL_INTERVAL = 2;
        private const int CONVERSION_POLL_COUNT = 10;

#if WINDOWS_PHONE
		private IScheduler scheduler = Scheduler.NewThread;
#endif
		private Delegate del;
		private Platform platform;
		private CoreListener listener;
		private Config config;
		private string accountName;
        private string secret;
		private StringBuilder postData = null;
		private HashSet<string> firingEvents = new HashSet<string>();
		private HashSet<string> firedEvents = new HashSet<string>();

		private string failingEventId = null;
		private int delay = 0;

		public Core(Delegate del, Platform platform, CoreListener listener, String accountName, String developerSecret, Config config)
		{
			this.del = del;
			this.platform = platform;
			this.listener = listener;
			this.config = config;
		
			this.accountName = Clean(accountName);
            this.secret = developerSecret;
			MakePostArgs();
		
			firedEvents = platform.LoadFiredEvents();
		}

		public void Start() {
#if TEST_WINPHONE || WINDOWS_PHONE
			string platformName = "winphone";
#else
			string platformName = "windows";
#endif

			string appName = platform.GetAppName();
			if (appName == null)
			{
				appName = "";
			}

			if (config.FireAutomaticInstallEvent)
			{
				if (config.InstallEventName != null)
				{
					FireEvent(new Event(config.InstallEventName, true));
				}
				else
				{
					FireEvent(new Event(string.Format("{0}-{1}-install", platformName, appName), true));
				}
			}

			if (config.FireAutomaticOpenEvent)
			{
				if (config.OpenEventName != null)
				{
					FireEvent(new Event(config.OpenEventName, false));
				}
				else
				{
					FireEvent(new Event(string.Format("{0}-{1}-open", platformName, appName), false));
				}
			}

            if(config.ConversionListener != null)
            {
                string url = String.Format(CONVERSION_URL_TEMPLATE, secret, platform.LoadUuid());
                int tries = 0;

#if WINDOWS_PHONE
                Action conversionCheck = null;
                conversionCheck = new Action(() =>
#else
                Action<Task> conversionCheck = null;
                conversionCheck = new Action<Task>((prevResult) =>
#endif
                {
                    tries++;
                    bool retry = true;

                    Response res = platform.Request(url, null, "GET");
                    if (res.Status >= 200 && res.Status < 300)
                    {
                        if (!new Regex("^\\s*\\[\\s*\\]\\s*$").IsMatch(res.Data))
                        {
                            retry = false;
                            config.ConversionListener.ConversionInfo(res.Data);
                        }
                    }

                    if (retry && tries <= CONVERSION_POLL_COUNT)
                    {
#if WINDOWS_PHONE
                        scheduler.Schedule(conversionCheck, TimeSpan.FromSeconds(CONVERSION_POLL_INTERVAL));
#else
                        Task.Delay(TimeSpan.FromSeconds(CONVERSION_POLL_INTERVAL)).ContinueWith(conversionCheck);
#endif
                    }
                });

#if WINDOWS_PHONE
				scheduler.Schedule(conversionCheck, TimeSpan.FromSeconds(CONVERSION_POLL_INTERVAL));
#else
                Task.Delay(TimeSpan.FromSeconds(CONVERSION_POLL_INTERVAL)).ContinueWith(conversionCheck);
#endif
            }
		}

		public void FireEvent(Event e)
		{
			lock (this)
			{
				// Notify the event that we are going to fire it so it can record the time
				e.Firing();

				if (e.OneTimeOnly)
				{
					if (firedEvents.Contains(e.Name))
					{
						Logging.Log(LogLevel.INFO, "Tapstream ignoring event named \"{0}\" because it is a one-time-only event that has already been fired", e.Name);
						listener.ReportOperation("event-ignored-already-fired", e.Name);
						listener.ReportOperation("job-ended", e.Name);
						return;
					}
					else if (firingEvents.Contains(e.Name))
					{
						Logging.Log(LogLevel.INFO, "Tapstream ignoring event named \"{0}\" because it is a one-time-only event that is already in progress", e.Name);
						listener.ReportOperation("event-ignored-already-in-progress", e.Name);
						listener.ReportOperation("job-ended", e.Name);
						return;
					}

					firingEvents.Add(e.Name);
				}

				Core self = this;
				string url = String.Format(EVENT_URL_TEMPLATE, accountName, e.EncodedName);
				string data = postData.ToString() + e.PostData;
				
				// Always ask the delegate what the delay should be, regardless of what our delay member says.
				// The delegate may wish to override it if this is a testing scenario.
				int actualDelay = del.GetDelay();

#if WINDOWS_PHONE
				scheduler.Schedule(new Action(() =>
				{
#else
				Task.Delay(TimeSpan.FromSeconds(actualDelay)).ContinueWith((prevResult) =>
				{
#endif
					Response response = platform.Request(url, data, "POST");
					bool failed = response.Status < 200 || response.Status >= 300;
					bool shouldRetry = response.Status < 0 || (response.Status >= 500 && response.Status < 600);
				
					lock(self)
					{
						if(e.OneTimeOnly)
						{
							self.firingEvents.Remove(e.Name);
						}
					
						if(failed)
						{
							// Only increase delays if we actually intend to retry the event
							if(shouldRetry)
							{
								// Not every job that fails will increase the retry delay.  It will be the responsibility of
								// the first failed job to increase the delay after every failure.
								if(delay == 0)
								{
									// This is the first job to fail, it must be the one to manage delay timing
									failingEventId = e.Uid;
									IncreaseDelay();
								}
								else if(failingEventId == e.Uid)
								{
									// This job is failing for a subsequent time
									IncreaseDelay();
								}
							}
						}
						else
						{
							if(e.OneTimeOnly)
							{
								self.firedEvents.Add(e.Name);
							
								platform.SaveFiredEvents(self.firedEvents);
								listener.ReportOperation("fired-list-saved", e.Name);
							}

							// Success of any event resets the delay
							delay = 0;
						}
					}

					if(failed)
					{
						if(response.Status < 0)
						{
							Logging.Log(LogLevel.ERROR, "Tapstream Error: Failed to fire event, error={0}", response.Message);
						}
						else if(response.Status == 404)
						{
							Logging.Log(LogLevel.ERROR, "Tapstream Error: Failed to fire event, http code {0}\nDoes your event name contain characters that are not url safe? This event will not be retried.", response.Status);
						}
						else if(response.Status == 403)
						{
							Logging.Log(LogLevel.ERROR, "Tapstream Error: Failed to fire event, http code {0}\nAre your account name and application secret correct?  This event will not be retried.", response.Status);
						}
						else
						{
							string retryMsg = "";
							if(!shouldRetry)
							{
								retryMsg = "  This event will not be retried.";
							}
							Logging.Log(LogLevel.ERROR, "Tapstream Error: Failed to fire event, http code {0}.{1}", response.Status, retryMsg);
						}

						listener.ReportOperation("event-failed", e.Name);
						if(shouldRetry)
						{
							listener.ReportOperation("retry", e.Name);
							listener.ReportOperation("job-ended", e.Name);
							if(del.IsRetryAllowed())
							{
								FireEvent(e);
							}
							return;
						}
					}
					else
					{
						Logging.Log(LogLevel.INFO, "Tapstream fired event named \"{0}\"", e.Name);
						listener.ReportOperation("event-succeeded", e.Name);
					}
				
					listener.ReportOperation("job-ended", e.Name);

#if WINDOWS_PHONE
				}), TimeSpan.FromSeconds(actualDelay));
#else
				});
#endif
			}
		}

#if WINDOWS_PHONE
		public void FireHit(Hit h, Hit.Complete completion)
		{
			string url = String.Format(HIT_URL_TEMPLATE, accountName, h.EncodedTrackerName);
			string data = h.PostData;
			scheduler.Schedule(new Action(() =>
			{
#else
		public IAsyncOperation<Response> FireHitAsync(Hit h)
		{
			string url = String.Format(HIT_URL_TEMPLATE, accountName, h.EncodedTrackerName);
			string data = h.PostData;
			return Task<Response>.Run(() =>
				{
#endif
					Response response = platform.Request(url, data, "POST");
					if(response.Status < 200 || response.Status >= 300)
					{
						Logging.Log(LogLevel.ERROR, "Tapstream Error: Failed to fire hit, http code: {0}", response.Status);
						listener.ReportOperation("hit-failed");
					}
					else
					{
						Logging.Log(LogLevel.INFO, "Fired hit to tracker: {0}", h.TrackerName);
						listener.ReportOperation("hit-succeeded");
					}

#if WINDOWS_PHONE
				if(completion != null)
				{
					completion(response);
				}
			}), TimeSpan.FromSeconds(0));
#else
					return response;
			}).AsAsyncOperation<Response>();
#endif
		}

		public string GetPostData()
		{
			return postData.ToString();
		}

		public int GetDelay()
		{
			return delay;
		}

		private string Clean(string s)
		{
			return Uri.EscapeDataString(s.ToLower().Trim());
		}

		private void IncreaseDelay()
		{
			if(delay == 0)
			{
				// First failure
				delay = 2;
			}
			else
			{
				// 2, 4, 8, 16, 32, 60, 60, 60...
				int newDelay = (int)Math.Pow(2, Math.Round(Math.Log(delay) / Math.Log(2)) + 1);
				delay = newDelay > 60 ? 60 : newDelay;
			}
			listener.ReportOperation("increased-delay");
		}

		private void AppendPostPair(string key, string value)
		{
			if (value == null)
			{
				return;
			}

			if (postData == null)
			{
				postData = new StringBuilder();
			}
			else
			{
				postData.Append("&");
			}
			postData.Append(Uri.EscapeDataString(key));
			postData.Append("=");
			postData.Append(Uri.EscapeDataString(value));
		}

		private void MakePostArgs()
		{
			AppendPostPair("secret", secret);
			AppendPostPair("sdkversion", VERSION);

			if (config.Hardware != null)
			{
				if (config.Hardware.Length > 255)
				{
					Logging.Log(LogLevel.WARN, "Tapstream Warning: Hardware argument exceeds 255 characters, it will not be included with fired events");
				}
				else
				{
					AppendPostPair("hardware", config.Hardware);
				}
			}

			if (config.Odin1 != null)
			{
				if (config.Odin1.Length > 255)
				{
					Logging.Log(LogLevel.WARN, "Tapstream Warning: ODIN-1 argument exceeds 255 characters, it will not be included with fired events");
				}
				else
				{
					AppendPostPair("hardware-odin1", config.Odin1);
				}
			}

#if TEST_WINPHONE || WINDOWS_PHONE
			if (config.CollectDeviceUniqueId)
			{
				AppendPostPair("hardware-winphone-device-unique-id", platform.GetDeviceUniqueId());
			}
#else
			if (config.CollectAppSpecificHardwareId)
			{
				AppendPostPair("hardware-windows-ashwid", platform.GetAppSpecificHardwareId());
			}
#endif

			AppendPostPair("uuid", platform.LoadUuid());

#if TEST_WINPHONE || WINDOWS_PHONE
			AppendPostPair("platform", "Windows Phone");
#else
			AppendPostPair("platform", "Windows");
#endif
			AppendPostPair("vendor", platform.GetManufacturer());
			AppendPostPair("model", platform.GetModel());
			AppendPostPair("os", platform.GetOs());
			AppendPostPair("resolution", platform.GetResolution());
			AppendPostPair("locale", platform.GetLocale());
			AppendPostPair("app-name", platform.GetAppName());
			AppendPostPair("package-name", platform.GetPackageName());
			AppendPostPair("gmtoffset", DateTimeOffset.Now.Offset.TotalSeconds.ToString());
		}

	}
}
