using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace TapstreamMetrics.Sdk
{
    public sealed class Utils
    {
        public static string EncodeString(string s)
        {
            if (s == null)
            {
                return null;
            }
            return Uri.EscapeDataString(s);
        }

        public static string Stringify(Object val)
        {
            if (val == null)
            {
                return null;
            }
            return val.ToString();
        }

        public static string EncodeEventPair(string prefix, string key, object val)
        {
            if (key == null || val == null)
            {
                return null;
            }

            if (key.Length > 255)
            {
                Logging.Log(LogLevel.WARN, "Tapstream Warning: Event key exceeds 255 characters, this field will not be included in the post (key={0})", key);
                return null;
            }

            string encodedName = Utils.EncodeString(prefix + key);
            if (encodedName == null)
            {
                return null;
            }

            string stringifiedValue = Utils.Stringify(val);
            if (stringifiedValue.Length > 255)
            {
                Logging.Log(LogLevel.WARN, "Tapstream Warning: Event value exceeds 255 characters, this field will not be included in the post (value={0})", val);
                return null;
            }

            string encodedValue = Utils.EncodeString(stringifiedValue);
            if (encodedValue == null)
            {
                return null;
            }

            return encodedName + "=" + encodedValue;
        }
    }
}
