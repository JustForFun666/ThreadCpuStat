/**
 * Created by  on 17-12-24.
 */
public class ThreadInfoBean implements Comparable<ThreadInfoBean>
{
    private int tid;

    private String tName;

    private int tCpuUsage;

    private int cnt;

    public ThreadInfoBean(int tid, int cnt, int tCpuUsage, String tName)
    {
        this.tid = tid;
        this.tName = tName;
        this.cnt = cnt;
        this.tCpuUsage = tCpuUsage;
    }

    public String toString()
    {
        return new StringBuilder(50)
                .append(tName)
                .append("    ")
                .append(tCpuUsage)
                .append("    ")
                .append(tid).toString();
    }

    @Override
    public int compareTo(ThreadInfoBean o)
    {
        return o.tCpuUsage - this.tCpuUsage;
    }
}
