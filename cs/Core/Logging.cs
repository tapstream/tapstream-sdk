using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace TapstreamMetrics.Sdk
{
    public enum LogLevel
    {
        INFO = 0,
        WARN,
        ERROR
    }

    public sealed class Logging
    {
        private class DefaultLogger : Logger
        {
            public void Log(LogLevel level, string msg)
            {
                System.Diagnostics.Debug.WriteLine(msg);
            }
        }

        private static Logger logger = new DefaultLogger();
        private static Object thisLock = new Object();
        

        public static void SetLogger(Logger logger)
        {
            lock (thisLock)
            {
                Logging.logger = logger;
            }
        }

        public static void Log(LogLevel level, string format, params Object[] args)
        {
            lock (thisLock)
            {
                if (logger != null)
                {
                    string msg = String.Format(format, args);
                    logger.Log(level, msg);
                }
            }
        }
    }
}
