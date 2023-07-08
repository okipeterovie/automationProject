package org.tooling.helper;


import lombok.extern.log4j.Log4j2;
import org.tooling.exception.WaitingFailureException;
import org.tooling.util.TestUtils;

import java.util.function.Supplier;


@Log4j2
public class Retry {


    private Retry() {
    }

    public static RetryBuilder times(int retryLimit) {
        return new RetryBuilder(retryLimit);
    }

    public static RetryBuilder lookFor(Class<? extends Throwable> ex) {
        return new RetryBuilder(ex);
    }

    public static RetryBuilder withTimeout(int timeoutInSeconds) {
        return new RetryBuilder(1).withTimeout(timeoutInSeconds);
    }

    public static void once(Runnable block) {
        new RetryBuilder(1).run(block);
    }

    public static void runAndIgnoreErrors(Runnable block) {
        new RetryBuilder(0).runAndIgnoreErrors(block);
    }

    public static void run(Class<? extends Throwable> exception, int retryLimit, Runnable block) {
        new RetryBuilder(retryLimit).lookFor(exception).run(block);
    }

    public static class RetryBuilder {

        private int retryLimit = 1;

        private Class<? extends Throwable> ex = Throwable.class;

        private Runnable block;

        private boolean ignoreExceptions = false;

        private boolean printExceptionStackTrace = false;

        private Supplier<Boolean> canStop = null;

        private double timeout = 0;

        private RetryBuilder(int value) {
            this.retryLimit = value;
        }

        private RetryBuilder(Class<? extends Throwable> ex) {
            this.ex = ex;
        }

        public RetryBuilder times(int retryLimit) {
            this.retryLimit = retryLimit;
            return this;
        }

        public RetryBuilder lookFor(Class<? extends Throwable> ex) {
            this.ex = ex;
            return this;
        }

        public RetryBuilder withTimeout(double timeoutInSeconds) {
            this.timeout = timeoutInSeconds;
            return this;
        }

        public RetryBuilder ignoringErrors() {
            this.ignoreExceptions = true;
            return this;
        }

        public RetryBuilder printingErrorStackTrace() {
            this.printExceptionStackTrace = true;
            return this;
        }

        public void till(Supplier<Boolean> canStop) {
            this.canStop = canStop;
            run();
        }

        private void run() {

            int count = 0;
            boolean done = false;
            do {
                try {
                    if (canStop != null) {
                        done = canStop.get();
                        if (!done) {
                            throw new WaitingFailureException();
                        }
                    } else {
                        block.run();
                        done = true;
                    }
                } catch (Throwable e) {
                    if (!ex.isInstance(e) || count == retryLimit) {
                        if (ignoreExceptions) {
                            log.debug("Exception ignored", e);
                        } else {
                            throw e;
                        }

                        if (printExceptionStackTrace) {
                            e.printStackTrace();
                        }
                    } else {
                        TestUtils.sleep(timeout);
                    }
                }
            } while (!done && (count++ < retryLimit));

            log.debug("took " + count + " attempts");

        }

        public void run(Runnable block) {
            this.block = block;
            run();
        }

        public void runAndIgnoreErrors(Runnable block) {
            this.block = block;
            this.ignoreExceptions = true;
            run();
        }

    }

}
