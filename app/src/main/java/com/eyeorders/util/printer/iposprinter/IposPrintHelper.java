package com.eyeorders.util.printer.iposprinter;



import org.jetbrains.annotations.NotNull;

public class IposPrintHelper {

    public static int CheckIposPrinter = 0x00000001;

    public static void setListener(@NotNull IposPrintHelper.Listener listener) {

    }


    public interface Listener {
        void onPrinterDisconnecting();

        void onError(Exception e);

        void onSuccess();
    }


    private Listener listener;
    public int iposPrinter = CheckIposPrinter;
}
