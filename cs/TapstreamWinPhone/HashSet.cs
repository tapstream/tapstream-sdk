using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Tapstream.Sdk
{
    class HashSet<T>
    {
        public Dictionary<T, bool> dict;

        public HashSet()
        {
            dict = new Dictionary<T, bool>();
        }

        public void Add(T value)
        {
            dict.Add(value, true);
        }

        public void Remove(T value)
        {
            dict.Remove(value);
        }

        public bool Contains(T value)
        {
            return dict.ContainsKey(value);
        }
    }
}
