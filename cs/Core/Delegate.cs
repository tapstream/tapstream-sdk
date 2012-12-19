using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Tapstream.Sdk
{
    interface Delegate
    {
        int GetDelay();
        bool IsRetryAllowed();
    }
}
