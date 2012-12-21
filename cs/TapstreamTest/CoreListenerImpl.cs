using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace TapstreamMetrics.Sdk
{
    class CoreListenerImpl : CoreListener
    {
        public OperationQueue queue;

        public CoreListenerImpl(OperationQueue q)
        {
            queue = q;
        }

        public void ReportOperation(string op)
        {
            queue.Add(new Operation(op, null));
        }

        public void ReportOperation(string op, string arg)
        {
            queue.Add(new Operation(op, arg));
        }
    }
}
