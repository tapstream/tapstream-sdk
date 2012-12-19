using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Collections.Concurrent;

using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace Tapstream.Sdk
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
	
	    public void Expect(string opName)
	    {
            Operation op = Take();
            Assert.AreEqual(opName, op.name);
	    }
    }
}
