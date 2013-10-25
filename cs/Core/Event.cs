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
            encodedName = Utils.EncodeString(this.name);
        }

        // This constructor is only to be used for creating custom IAP events.
        public Event(string name, string transactionId, string productId, int quantity, int priceInCents, string currencyCode)
            : this(name, false)
        {
            AddPair("", "purchase-transaction-id", transactionId);
            AddPair("", "purchase-product-id", productId);
            AddPair("", "purchase-quantity", quantity);
            AddPair("", "purchase-price", priceInCents);
            AddPair("", "purchase-currency", currencyCode);
        }

        public void AddPair(string key, Object value)
        {
            AddPair("custom-", key, value);
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

        private void AddPair(string prefix, string key, Object value)
        {
            string encodedPair = Utils.EncodeEventPair("custom-", key, value);
            if (encodedPair == null)
            {
                return;
            }

            if (postData == null)
            {
                postData = new StringBuilder();
            }
            postData.Append("&");
            postData.Append(encodedPair);
        }
    }
}
