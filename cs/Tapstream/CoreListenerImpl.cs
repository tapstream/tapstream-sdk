using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

#if WINDOWS_PHONE
#else
using System.Threading.Tasks;
#endif

namespace TapstreamMetrics.Sdk
{
    class CoreListenerImpl : CoreListener
    {
        public void ReportOperation(string op)
        {
        }

        public void ReportOperation(string op, string arg)
        {
        }
    }
}
