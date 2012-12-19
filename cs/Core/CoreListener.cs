using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Tapstream.Sdk
{
    interface CoreListener
    {
        void ReportOperation(string op);
        void ReportOperation(string op, string arg);
    }
}
