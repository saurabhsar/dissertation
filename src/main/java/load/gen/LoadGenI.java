package load.gen;

public interface LoadGenI {
    void initialize(boolean transactional);

    void run();
}
