package com.eyeorders.util.printer.iposprinter;

/**
 * Created by Hashim Al-Haddadi on 08/10/2021
 */
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.SoftReference;
public class HandlerUtils {

    private static final long serialVersionUID = 0L;

    /**
     * 在使用handler的地方继承此接口，然后把实例化的引用给实例化的handler
     */
    public interface IHandlerIntent {
        void handlerIntent(Message message);
    }

    public static final class PrinterHandler extends Handler
    {
        private SoftReference<IHandlerIntent> owner;

        public PrinterHandler(IHandlerIntent t) {
            owner = new SoftReference<>(t);
        }

        public PrinterHandler(Looper looper, IHandlerIntent t) {
            super(looper);
            owner = new SoftReference<>(t);
        }

        @Override
        public void handleMessage(Message msg) {
            IHandlerIntent t = owner.get();
            if (null != t) {
                t.handlerIntent(msg);
            }
        }
    }

}
