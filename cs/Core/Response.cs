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

        public Response(int status, string message)
        {
            this.status = status;
            this.message = message;
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
    }
}
