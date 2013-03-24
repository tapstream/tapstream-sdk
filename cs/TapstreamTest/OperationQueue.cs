using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Collections.Concurrent;

using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace TapstreamMetrics.Sdk
{
    public sealed class Operation
    {
        public string name;
        public string arg;

        public Operation(string name, string arg)
        {
            this.name = name;
            this.arg = arg;
        }
    }

    public sealed class OperationQueue : BlockingCollection<Operation>
    {
        public OperationQueue()
	    {
	    }
	
	    public string Expect(string opName)
	    {
            Operation op = Take();
            Assert.AreEqual(opName, op.name);
            return op.arg;
	    }

        public string ExpectEventually(string opName)
        {
            while(true)
            {
                Operation op = Take();
                if (opName == op.name)
                {
                    return op.arg;
                }
            }
        }
    }
}
