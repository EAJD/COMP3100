public class Job {
    public int id;
    public int estRunTime;
    public int core;
    public int memory;
    public int disk;

    public Job(String[] jobString) {
        // jobString is "JOBN submitTime jobID estRuntime core memory disk"
        id = Integer.parseInt(jobString[2]);
        estRunTime = Integer.parseInt(jobString[3]);
        core = Integer.parseInt(jobString[4]);
        memory = Integer.parseInt(jobString[5]);
        disk = Integer.parseInt(jobString[6]);
    }
}