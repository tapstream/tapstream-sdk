using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

#if WINDOWS_PHONE
#else
using Windows.Foundation;
using System.Threading.Tasks;
#endif

namespace Tapstream.Sdk
{
    public interface Api
    {
        void FireEvent(Event e);

#if WINDOWS_PHONE
        void FireHit(Hit h, Hit.Complete completion);
#else
        IAsyncOperation<Response> FireHitAsync(Hit h);
#endif
    }
}
