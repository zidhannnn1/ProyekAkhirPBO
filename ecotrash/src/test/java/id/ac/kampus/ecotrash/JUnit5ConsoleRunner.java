package id.ac.kampus.ecotrash;

import org.junit.jupiter.engine.JupiterTestEngine;
import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.DiscoveryFilter;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.discovery.DiscoverySelectors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Runner JUnit 5 berbasis JupiterTestEngine resmi untuk mengeksekusi seluruh
 * kelas pengujian @Test JUnit 5 melalui command line atau script ./run_tests.sh.
 */
public class JUnit5ConsoleRunner {

    public static void main(String[] args) {
        JupiterTestEngine engine = new JupiterTestEngine();
        List<DiscoverySelector> selectors = List.of(
                DiscoverySelectors.selectClass(NasabahWargaTest.class),
                DiscoverySelectors.selectClass(KategoriDanTransaksiUnitTest.class),
                DiscoverySelectors.selectClass(BankSampahIntegrasiTest.class));

        EngineDiscoveryRequest request = new SimpleDiscoveryRequest(selectors);
        TestDescriptor root = engine.discover(request, UniqueId.forEngine("junit-jupiter"));

        AtomicInteger total = new AtomicInteger();
        AtomicInteger passed = new AtomicInteger();
        AtomicInteger failed = new AtomicInteger();
        List<String> failureLogs = new ArrayList<>();

        EngineExecutionListener listener = new EngineExecutionListener() {
            @Override
            public void executionFinished(TestDescriptor descriptor, TestExecutionResult result) {
                if (descriptor.isTest()) {
                    total.incrementAndGet();
                    String parent = descriptor.getParent()
                            .map(TestDescriptor::getDisplayName)
                            .orElse("Test");
                    if (result.getStatus() == TestExecutionResult.Status.SUCCESSFUL) {
                        passed.incrementAndGet();
                        System.out.println("[OK] " + parent + " :: " + descriptor.getDisplayName());
                    } else {
                        failed.incrementAndGet();
                        System.out.println("[GAGAL] " + parent + " :: " + descriptor.getDisplayName());
                        StringBuilder sb = new StringBuilder();
                        sb.append("[GAGAL] ").append(parent).append(" :: ")
                                .append(descriptor.getDisplayName()).append("\n");
                        result.getThrowable().ifPresent(t -> appendTrace(sb, t));
                        failureLogs.add(sb.toString());
                    }
                }
            }
        };

        System.out.println("=== MENJALANKAN JUNIT 5 TEST SUITE ECOTRASH v4 ===\n");
        engine.execute(ExecutionRequest.create(root, listener, request.getConfigurationParameters()));

        if (!failureLogs.isEmpty()) {
            System.out.println("\n--- RINCIAN KEGAGALAN (STACK TRACE) ---");
            for (String log : failureLogs) {
                System.out.println(log);
            }
        }

        System.out.printf("\n%d tes, %d lulus, %d gagal\n", total.get(), passed.get(), failed.get());
        if (failed.get() > 0) {
            System.exit(1);
        }
    }

    private static void appendTrace(StringBuilder sb, Throwable t) {
        sb.append("  ").append(t).append("\n");
        StackTraceElement[] stack = t.getStackTrace();
        for (int i = 0; i < Math.min(stack.length, 6); i++) {
            sb.append("    at ").append(stack[i]).append("\n");
        }
    }

    private static class SimpleDiscoveryRequest implements EngineDiscoveryRequest {
        private final List<DiscoverySelector> selectors;
        private final ConfigurationParameters params = new ConfigurationParameters() {
            @Override
            public Optional<String> get(String key) { return Optional.empty(); }
            @Override
            public Optional<Boolean> getBoolean(String key) { return Optional.empty(); }
            @Override
            @SuppressWarnings("deprecation")
            public int size() { return 0; }
            @Override
            public Set<String> keySet() { return Set.of(); }
        };

        SimpleDiscoveryRequest(List<DiscoverySelector> selectors) {
            this.selectors = selectors;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T extends DiscoverySelector> List<T> getSelectorsByType(Class<T> selectorType) {
            List<T> matched = new ArrayList<>();
            for (DiscoverySelector s : selectors) {
                if (selectorType.isInstance(s)) {
                    matched.add((T) s);
                }
            }
            return matched;
        }

        @Override
        public <T extends DiscoveryFilter<?>> List<T> getFiltersByType(Class<T> filterType) {
            return List.of();
        }

        @Override
        public ConfigurationParameters getConfigurationParameters() {
            return params;
        }
    }
}
