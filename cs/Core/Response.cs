using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace TapstreamMetrics.Sdk
{
    public sealed class Response
    {
        private int status;
        private string message;
        private string data;

        public Response(int status, string message, string data)
        {
            this.status = status;
            this.message = message;
            this.data = data;
        }

        public int Status
        {
            get
            {
                return status;
            }
        }

        public string Message
        {
            get
            {
                return message;
            }
        }

        public string Data
        {
            get
            {
                return data;
            }
        }
    }
}
