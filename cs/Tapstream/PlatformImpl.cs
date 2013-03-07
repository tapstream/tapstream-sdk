using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

#if WINDOWS_PHONE
using Microsoft.Phone.Reactive;
using Microsoft.Phone.Info;
using System.Threading;
using System.Net;
using System.IO;
using System.IO.IsolatedStorage;
#else
using System.Threading.Tasks;
using System.Net.Http;
using Windows.Globalization;
#endif

namespace TapstreamMetrics.Sdk
{
    class PlatformImpl : Platform
    {
        private const string FIRED_EVENTS_KEY = "__tapstream_sdk_fired_events";
        private const string UUID_KEY = "__tapstream_sdk_uuid";

        public string LoadUuid()
        {
#if WINDOWS_PHONE
            string guid = null;
            if(IsolatedStorageSettings.ApplicationSettings.TryGetValue<string>(UUID_KEY, out guid))
            {
                return guid;
            }
            guid = Guid.NewGuid().ToString();
            IsolatedStorageSettings.ApplicationSettings[UUID_KEY] = guid;
            IsolatedStorageSettings.ApplicationSettings.Save();
            return guid;
#else
            Windows.Storage.ApplicationDataContainer localSettings = Windows.Storage.ApplicationData.Current.LocalSettings;
            if(localSettings.Values.ContainsKey(UUID_KEY))
            {
                return (string)localSettings.Values[UUID_KEY];
            }
            string guid = Guid.NewGuid().ToString();
            localSettings.Values[UUID_KEY] = guid;
            return guid;            
#endif
        }

        public HashSet<string> LoadFiredEvents()
        {
#if WINDOWS_PHONE
            HashSet<string> firedEvents = new HashSet<string>();
            Dictionary<string, bool> contents;
            if(IsolatedStorageSettings.ApplicationSettings.TryGetValue<Dictionary<string, bool>>(FIRED_EVENTS_KEY, out contents))
            {
                firedEvents.dict = contents;
            }
            return firedEvents;
#else
            Windows.Storage.ApplicationDataContainer localSettings = Windows.Storage.ApplicationData.Current.LocalSettings;
            if(!localSettings.Values.ContainsKey(FIRED_EVENTS_KEY))
            {
                return new HashSet<string>();
            }
            return new HashSet<string>((string[])localSettings.Values[FIRED_EVENTS_KEY]);
#endif
        }

        public void SaveFiredEvents(HashSet<string> firedEvents)
        {
#if WINDOWS_PHONE
            IsolatedStorageSettings.ApplicationSettings[FIRED_EVENTS_KEY] = firedEvents.dict;
            IsolatedStorageSettings.ApplicationSettings.Save();
#else
            Windows.Storage.ApplicationDataContainer localSettings = Windows.Storage.ApplicationData.Current.LocalSettings;
            localSettings.Values[FIRED_EVENTS_KEY] = firedEvents.ToArray();
#endif
        }


        public string GetResolution()
        {
#if WINDOWS_PHONE
            int w = (int)System.Windows.Application.Current.Host.Content.ActualWidth;
            int h = (int)System.Windows.Application.Current.Host.Content.ActualHeight;
            return String.Format("{0}x{1}", w, h);
#else
            return "";
#endif
        }

        public string GetManufacturer()
        {
#if WINDOWS_PHONE
            object manufacturerObject;
            if(DeviceExtendedProperties.TryGetValue("DeviceManufacturer", out manufacturerObject))
            {
                return (string)manufacturerObject;
            }
            return "";
#else
            return "Microsoft";
#endif
        }

        public string GetModel()
        {
#if WINDOWS_PHONE
            object modelObject;
            if(DeviceExtendedProperties.TryGetValue("DeviceName", out modelObject))
            {
                return (string)modelObject;
            }
            return "";
#else
            return "";
#endif
        }

        public string GetOs()
        {
#if WINDOWS_PHONE
            return System.Environment.OSVersion.ToString();
#else
            return "Windows 8";
#endif
        }

        public string GetLocale()
        {
#if WINDOWS_PHONE
            return String.Format("{0}_{1}", System.Globalization.CultureInfo.CurrentCulture.TwoLetterISOLanguageName, System.Globalization.RegionInfo.CurrentRegion.TwoLetterISORegionName);
#else
            string locale = "unknown";
            try
            {
                locale = ApplicationLanguages.Languages.ElementAt(0);
                locale = locale.Replace("-", "_");
            }
            catch (Exception) { }
            return locale;
#endif
        }

        public Response Request(string url, string data)
        {
#if WINDOWS_PHONE
            int status = -1;
            string message = null;
            AutoResetEvent signal = new AutoResetEvent(false);
            
            HttpWebRequest req = (HttpWebRequest)HttpWebRequest.CreateHttp(url);
            req.Method = "POST";
            req.ContentType = "application/x-www-form-urlencoded;charset=UTF-8";
            try
            {
                req.BeginGetRequestStream(reqState =>
                {
                    Stream postStream = req.EndGetRequestStream(reqState);

                    Encoding enc = new System.Text.UTF8Encoding();
                    postStream.Write(enc.GetBytes(data), 0, data.Length);
                    postStream.Close();

                    req.BeginGetResponse(respState =>
                    {
                        try
                        {
                            HttpWebResponse response = (HttpWebResponse)req.EndGetResponse(respState);
                            status = (int)response.StatusCode;
                            message = response.StatusDescription;
                        }
                        catch (WebException we)
                        {
                            var resp = we.Response as HttpWebResponse;
                            if (resp == null)
                            {
                                throw;
                            }
                            status = (int)resp.StatusCode;
                            message = we.ToString();
                        }
                        catch (Exception ex)
                        {
                            status = -1;
                            message = ex.ToString();
                        }

                        signal.Set();

                    }, null);

                }, null);
            }
            catch (Exception ex)
            {
                status = -1;
                message = ex.ToString();
            }

            signal.WaitOne();
            return new Response(status, message);
#else

            int status = -1;
            string message = null;
            try
            {
                using (HttpClient client = new HttpClient())
                {
                    HttpResponseMessage response = client.PostAsync(url, new StringContent(data, Encoding.UTF8, "application/x-www-form-urlencoded")).Result;
                    status = (int)response.StatusCode;
                    if(!response.IsSuccessStatusCode)
                    {
                        message = response.ReasonPhrase;
                    }
                }
            }
            catch (Exception ex)
            {
                status = -1;
                while (ex.InnerException != null)
                {
                    ex = ex.InnerException;
                }
                message = ex.Message;
            }
            return new Response(status, message);
#endif
        }
    }
}
