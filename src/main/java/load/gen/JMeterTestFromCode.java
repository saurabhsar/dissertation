package load.gen;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.control.IfController;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.SetupThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

import java.io.IOException;


public class JMeterTestFromCode {
    private StandardJMeterEngine jmeter;

    void initialize() {
        jmeter = new StandardJMeterEngine();
        JMeterUtils.loadJMeterProperties("jmeter.properties");
        JMeterUtils.loadProperties("user.properties");
        JMeterUtils.setJMeterHome(".");
        JMeterUtils.setProperty("saveservice_properties", "/saveservice.properties");
    }

    void hit() {

    }

    public static HashTree create(int loop, int threads, int rampUp) throws IOException {

        HashTree projectTree = new HashTree();

        HTTPSampler googleGetSampler = createHttpSampler("POST", "localhost", 1729, "/");
        LoopController loopCtrl = createLoopController(loop);
        IfController ifController = createIfController("");
        loopCtrl.addTestElement(googleGetSampler);
        SetupThreadGroup setupThreadGroup = createSetupThreadGroup(loopCtrl, threads, rampUp);
        TestPlan testPlan = createTestPlan("Simple Test Plan");
        testPlan.addThreadGroup(setupThreadGroup);

        HashTree testPlanTree = projectTree.add(testPlan);
        HashTree setupThreadGroupTree = testPlanTree.add(setupThreadGroup);
        HashTree loopCtrlTree = setupThreadGroupTree.add(loopCtrl);
        loopCtrlTree.add(googleGetSampler);
        return projectTree;
    }


    private static HTTPSampler createHttpSampler(String method, String domain, int port, String path) {
        HTTPSampler googleGetSampler = new HTTPSampler();
        googleGetSampler.setDomain(domain);
        googleGetSampler.setPort(port);
        googleGetSampler.setPath(path);
        googleGetSampler.setMethod(method);
        googleGetSampler.setName(String.format("%s %s %s", domain, method, path));
        return enhanceWithGuiClass(googleGetSampler);
    }

    private static TestPlan createTestPlan(String name) {
        TestPlan testPlan = new TestPlan(name);
        testPlan.setEnabled(true);
        testPlan.setComment("");
        testPlan.setFunctionalMode(false);
        testPlan.setSerialized(false);
        testPlan.setUserDefinedVariables(new Arguments());
        testPlan.setTestPlanClasspath("");
        return enhanceWithGuiClass(testPlan);
    }

    private static <T extends TestElement> T enhanceWithGuiClass(T testElement) {
        testElement.setProperty(TestElement.GUI_CLASS, " "/*testElement.getClass().getName()+"Gui"*/);
        return testElement;
    }

    private static LoopController createLoopController(int loops) {
        LoopController loopCtrl = new LoopController();
        loopCtrl.setLoops(loops);
        loopCtrl.setFirst(true);
        return enhanceWithGuiClass(loopCtrl);
    }

    private static SetupThreadGroup createSetupThreadGroup(LoopController loopCtrl, int numThreads, int rampUp) {
        SetupThreadGroup threadGroup = new SetupThreadGroup();
        threadGroup.setNumThreads(numThreads);
        threadGroup.setRampUp(rampUp);
        threadGroup.setSamplerController(loopCtrl);
        return enhanceWithGuiClass(threadGroup);
    }

    private static IfController createIfController(String condition) {
        IfController ifController = new IfController(condition);
        return ifController;
    }
}
