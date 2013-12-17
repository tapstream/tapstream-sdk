using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace TapstreamMetrics.Sdk
{
    public sealed class Event
    {
        private static Random rng = new Random();

        private double firstFiredTime = 0;
        private string uid;
        private string name;
        private string encodedName;
        private bool oneTimeOnly;
        private StringBuilder postData = new StringBuilder();
        internal IDictionary<string, object> customFields = new Dictionary<string, object>();

        private bool isTransaction = false;
        private string productId;

        public Event(string name, bool oneTimeOnly)
        {
            uid = MakeUid();
            SetName(name);
            this.oneTimeOnly = oneTimeOnly;
        }

        // This constructor is only to be used for creating custom IAP events.
        public Event(string transactionId, string productId, int quantity, int priceInCents, string currencyCode)
            : this("", false)
        {
            this.productId = productId;
            isTransaction = true;
            AddPair("", "purchase-transaction-id", transactionId);
            AddPair("", "purchase-product-id", productId);
            AddPair("", "purchase-quantity", quantity);
            AddPair("", "purchase-price", priceInCents);
            AddPair("", "purchase-currency", currencyCode);
        }

        // This constructor is only to be used for creating custom IAP events.
        public Event(string transactionId, string productId, int quantity)
            : this("", false)
        {
            this.productId = productId;
            isTransaction = true;
            AddPair("", "purchase-transaction-id", transactionId);
            AddPair("", "purchase-product-id", productId);
            AddPair("", "purchase-quantity", quantity);
        }

        public void AddPair(string key, Object value)
        {
            customFields.Add(key, value);
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
                return postData != null ? postData.ToString() : "";
            }
        }

        internal bool IsTransaction
        {
            get
            {
                return isTransaction;
            }
        }

        internal void Prepare(IDictionary<string, object> globalEventParams)
        {
            // Only record the time of the first fire attempt
            if(firstFiredTime == 0)
            {
                TimeSpan t = (DateTime.UtcNow - new DateTime(1970, 1, 1));
                firstFiredTime = t.TotalMilliseconds;

                foreach (string key in globalEventParams.Keys)
                {
                    if (!customFields.ContainsKey(key))
                    {
                        customFields.Add(key, globalEventParams[key]);
                    }
                }

                postData.Append(String.Format("&created-ms={0:0.}", firstFiredTime));

                foreach (string key in customFields.Keys)
                {
                    AddPair("custom-", key, customFields[key]);
                }
            }
        }

        internal void SetName(string eventName)
        {
            name = eventName.ToLower().Trim().Replace(".", "_");
            encodedName = Utils.EncodeString(name);
        }

        internal void SetNamePrefix(string platform, string appName)
        {
            SetName(String.Format("{0}-{1}-purchase-{2}", platform, appName.ToLower().Trim(), productId));
        }

        private string MakeUid()
        {
            return String.Format("{0}:{1}", Environment.TickCount, rng.NextDouble());
        }

        private void AddPair(string prefix, string key, Object value)
        {
            string encodedPair = Utils.EncodeEventPair(prefix, key, value);
            if (encodedPair != null)
            {
                postData.Append("&");
                postData.Append(encodedPair);
            }
        }
    }
}
