using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace TapstreamMetrics.Sdk
{
    public sealed class Event
    {
        private static Random rng = new Random();

        private uint firstFiredTime = 0;
        private string uid;
        private string name;
        private string encodedName;
        private bool oneTimeOnly;
        private StringBuilder postData = null;

        public Event(string name, bool oneTimeOnly)
        {
            uid = MakeUid();
            this.name = name.ToLower().Trim();
            this.oneTimeOnly = oneTimeOnly;
            encodedName = Uri.EscapeDataString(this.name);
        }

        public void AddPair(string key, Object value)
        {
            if(value == null)
            {
                return;
            }

            if(key.Length > 255)
            {
                Logging.Log(LogLevel.WARN, "Tapstream Warning: Custom key exceeds 255 characters, this field will not be included in the post (key={0})", key);
                return;
            }
            string encodedName = Uri.EscapeDataString("custom-" + key);
            
            string stringifiedValue = value.ToString();
            if(stringifiedValue.Length > 255)
            {
                Logging.Log(LogLevel.WARN, "Tapstream Warning: Custom value exceeds 255 characters, this field will not be included in the post (value={0})", value);
                return;
            }
            string encodedValue = Uri.EscapeDataString(stringifiedValue);
            
            if (postData == null)
            {
                postData = new StringBuilder();
            }
            postData.Append("&");
            postData.Append(encodedName);
            postData.Append("=");
            postData.Append(encodedValue);
        }

        public string Uid
        {
            get
            {
                return uid;
            }
        }

        public string Name
        {
            get
            {
                return name;
            }
        }

        public string EncodedName
        {
            get
            {
                return encodedName;
            }
        }

        public bool OneTimeOnly
        {
            get
            {
                return oneTimeOnly;
            }
        }

        public string PostData
        {
            get
            {
                string result = postData != null ? postData.ToString() : "";
                return String.Format("&created-ms={0}", firstFiredTime) + result;
            }
        }

        internal void Firing()
        {
            // Only record the time of the first fire attempt
            if(firstFiredTime == 0)
            {
                TimeSpan t = (DateTime.UtcNow - new DateTime(1970, 1, 1));
                firstFiredTime = (uint)t.TotalMilliseconds;
            }
        }

        private string MakeUid()
        {
            return String.Format("{0}:{1}", Environment.TickCount, rng.NextDouble());
        }
    }
}
