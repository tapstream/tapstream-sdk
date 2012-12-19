using System;

#if WINDOWS_PHONE
#else
using Windows.Foundation;
using System.Threading.Tasks;
#endif

namespace Tapstream.Sdk
{
    public sealed class ConversionTracker : Api
    {
        private static ConversionTracker instance;
        private static object instanceLock = new Object();
        
        public static void Create(string accountName, string developerSecret)
        {
            Create(accountName, developerSecret, null);
        }
        
        public static void Create(string accountName, string developerSecret, string hardware)
        {
            lock (instanceLock)
            {
                if (instance == null)
                {
                    instance = new ConversionTracker(accountName, developerSecret, hardware);
                }
                else
                {
                    Logging.Log(LogLevel.WARN, "ConversionTracker Warning: ConversionTracker already instantiated, it cannot be re-created.");
                }
            }
        }

        public static ConversionTracker Instance
        {
            get
            {
                lock (instanceLock)
                {
                    if (instance == null)
                    {
                        throw new Exception("You must first call ConversionTracker.Create");
                    }
                    return instance;
                }
            }
        }


        private class DelegateImpl : Delegate
        {
            private ConversionTracker ts;
            
            public DelegateImpl(ConversionTracker ts)
            {
                this.ts = ts;
            }

            public int GetDelay()
            {
                return ts.core.GetDelay();
            }

            public bool IsRetryAllowed()
            {
                return true;
            }
        }


        private Delegate del;
        private Platform platform;
        private CoreListener listener;
        private Core core;

        private ConversionTracker(string accountName, string developerSecret, string hardware)
        {
            del = new DelegateImpl(this);
            platform = new PlatformImpl();
            listener = new CoreListenerImpl();
            core = new Core(del, platform, listener, accountName, developerSecret, hardware);
        }

        public void FireEvent(Event e)
        {
            core.FireEvent(e);
        }

#if WINDOWS_PHONE
        public void FireHit(Hit h, Hit.Complete completion)
        {
            core.FireHit(h, completion);
        }
#else
        public IAsyncOperation<Response> FireHitAsync(Hit h)
        {
            return core.FireHitAsync(h);
        }
#endif
    }
}
