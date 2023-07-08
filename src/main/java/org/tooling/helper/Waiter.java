package org.tooling.helper;

import lombok.extern.log4j.Log4j2;
import org.tooling.exception.NotSupportedException;
import org.tooling.exception.WaitingFailureException;
import org.tooling.util.TestUtils;

import java.util.function.Supplier;

@Log4j2
public class Waiter {

    private Waiter() {
    }

    public static WaitBuilder forSeconds(int timeout) {
        return new WaitBuilder().forSeconds(timeout);
    }

    public static WaitBuilder pollEvery(int pollIntervalInMilliSeconds) {
        return new WaitBuilder().pollEvery(pollIntervalInMilliSeconds);
    }

    public static void start(int timeoutInSeconds, int pollIntervalInMilliSeconds, Supplier<Boolean> block) {
        new WaitBuilder().pollEvery(pollIntervalInMilliSeconds).forSeconds(timeoutInSeconds).waitFor(block).perform();
    }

    public static void start(Supplier<Boolean> block) {
        new WaitBuilder().waitFor(block).perform();
    }

    public static class WaitThread {

        Thread thread;
        WaitBuilder waitBuilder;

        public WaitThread(WaitBuilder builder, Thread thread) {
            this.waitBuilder = builder;
            this.thread = thread;
        }

        public boolean getWaitResult() {
            join();
            return !waitBuilder.isFailure;
        }

        public boolean getWaitResultAsync() {
            return !waitBuilder.isFailure;
        }

        public void join() {
            try {
                thread.join();
            } catch (InterruptedException e) {
                log.error(e);
            }
        }
    }

    public static class WaitBuilder {

        boolean isFailure;
        private Supplier<Boolean> waitStatusProvider = () -> false;
        private Runnable successAction = null;
        private Runnable failureAction = null;
        private long timeout = 30000;
        private long pollInterval = 1000;

        private WaitBuilder() {
        }

        public WaitBuilder forSeconds(int timeout) {
            if (timeout < 1) {
                throw new NotSupportedException("Cannot handle negative values for timeout");
            }
            this.timeout = timeout * (long) 1000;
            return this;
        }

        public WaitBuilder onSuccess(Runnable block) {
            this.successAction = block;
            return this;
        }

        public WaitBuilder onFailure(Runnable block) {
            this.failureAction = block;
            return this;
        }

        public WaitBuilder ignoringFailure() {
            this.failureAction = () -> {
            };
            return this;
        }

        public WaitBuilder waitFor(Supplier<Boolean> block) {
            this.waitStatusProvider = block;
            return this;
        }

        public WaitBuilder pollEvery(int timeInMilliSeconds) {
            if (timeInMilliSeconds < 1) {
                throw new NotSupportedException("Cannot handle negative values for poll interval");
            }
            if (timeInMilliSeconds >= timeout) {
                log.warn("Poll interval cannot be greater than or equal to the timeout. Increase the timout first. Poll interval will be set to 100ms lesser than the timout.");
                this.pollInterval = timeout - 100;
            } else {
                this.pollInterval = timeInMilliSeconds;
            }
            return this;
        }

        private boolean isTimeout(Long startTime) {
            long interval = System.currentTimeMillis() - startTime;
            return interval >= timeout;
        }

        private void doWait() {
            boolean isSuccessBlockProvided = successAction != null;
            boolean isFailureBlockProvided = failureAction != null;
            Long startTime = System.currentTimeMillis();

            while (isFailure = !waitStatusProvider.get()) {

                TestUtils.sleepInMillis(pollInterval);

                if (isTimeout(startTime)) {
                    if (isFailureBlockProvided) {
                        failureAction.run();
                        break;
                    }
                    throw new WaitingFailureException();
                }
            }

            if (!isFailure && isSuccessBlockProvided) {
                successAction.run();
            }

        }

        public void perform() {
            this.doWait();
        }

        public WaitThread performAsync() {
            Thread thread = new Thread(this::doWait);
            thread.start();
            return new WaitThread(this, thread);
        }

    }

}
