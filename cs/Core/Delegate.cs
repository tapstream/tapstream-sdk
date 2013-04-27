using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace TapstreamMetrics.Sdk
{
    interface Delegate
    {
        int GetDelay();
        void SetDelay(int delay);
        bool IsRetryAllowed();
    }
}
