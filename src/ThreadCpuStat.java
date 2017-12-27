import java.util.*;

/**
 * Created by  on 17-12-24.
 */
public class ThreadCpuStat
{
    /**
     * 进程pid
     */
    private int pid = Integer.MIN_VALUE;

    /**
     * 监控时长
     */
    private int period = Integer.MIN_VALUE;

    /**
     * key：PId
     * Value：每个周期内对应pid的cpu占用率
     */
    HashMap<Integer, List<Float>> threadCpuUasgeMap = new HashMap<Integer, List<Float>>(10);

    public static void main(String[] args)
    {
        ThreadCpuStat stat = new ThreadCpuStat();
        if (!stat.init(args))
        {
            System.out.println("args error: TreadCpuStat [PID] [PERIOD]");
            return;
        }

        for (int i = 0; i < stat.getPeriod(); i++)
        {
            long bCurTime = System.currentTimeMillis();
            stat.getThreadCpuUsage();
            long intv = System.currentTimeMillis() - bCurTime;

            if (intv < 3000)
            {
                try
                {
                    Thread.sleep(3000 - intv);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

        }

        stat.outPutStatResult();
    }

    /**
     * 解析入参
     * @param args 入参
     * @return 解析入参的结果
     */
    private boolean init(String[] args)
    {
        if (null == args || 2 != args.length)
        {
            return false;
        }

        try
        {
            this.pid = Integer.parseInt(args[0]);
            int periodTmp = Integer.parseInt(args[1]) / 3;
            this.period = periodTmp > 1200 ? 1200 : periodTmp;
        }
        catch (NumberFormatException e)
        {
            System.out.println(e.getMessage());
            return false;
        }

        return true;


    }

    /**
     * 通过执行top -b -n1 -p 进程pid -H 来获取要统计进程中包含的线程的cpu使用情况。
     * @return
     */
    private Map<Integer, List<Float>> getThreadCpuUsage()
    {
        List<String> cmdResult = StatTool.execute("top -b -n1 -Hp " + pid);
        if (null == cmdResult)
        {
            return null;
        }

        boolean canRead = false;
        for (String line : cmdResult)
        {
            if (null == line || "".equals(line.trim()))
            {
                continue;
            }

            if (-1 != line.indexOf("PID") && -1 != line.indexOf("CPU"))
            {
                canRead = true;
                continue;
            }

            if (canRead)
            {

                String [] strs = line.trim().replaceAll( "\\s+ ", " " ).split(" ");

                if (null == strs || strs.length < 9)
                {
                    continue;
                }

                try
                {
                    int threadid = Integer.parseInt(strs[0]);
                    float cpuUsage = Float.parseFloat(strs[8]);
                    List<Float> cpuUsages = threadCpuUasgeMap.get(threadid);
                    if (null == cpuUsages)
                    {
                        cpuUsages =  new ArrayList<Float>(1);
                        threadCpuUasgeMap.put( threadid, cpuUsages );
                    }

                    cpuUsages.add(cpuUsage);
                }
                catch (NumberFormatException e)
                {
                    System.out.println(e.getMessage());
                    continue;
                }

            }
        }

        return threadCpuUasgeMap;
    }

    private void outPutStatResult()
    {
        Map<Integer, String> pid2NameMap = getPid2NameMap();
        if (null == pid2NameMap || null == threadCpuUasgeMap)
        {
            return;
        }

        List<ThreadInfoBean> threadInfos = new ArrayList<ThreadInfoBean>(5);
        for (Map.Entry<Integer, List<Float>> entry : threadCpuUasgeMap.entrySet())
        {
            threadInfos.add(new ThreadInfoBean(entry.getKey(),
                    entry.getValue().size(),
                    StatTool.getAveOfInts(entry.getValue()),
                    pid2NameMap.get(entry.getKey())));
        }

        Collections.sort(threadInfos);
        for (ThreadInfoBean bean: threadInfos)
        {
            System.out.println(bean);
        }
    }



    private Map<Integer, String> getPid2NameMap()
    {
        List<String> jstackResults = StatTool.execute("jstack " + pid);
        if (null == jstackResults || 0 == jstackResults.size())
        {
            return null;
        }

        Map<Integer, String> pid2NameMap = new HashMap<Integer, String>(10);
        for (String line : jstackResults)
        {
            if (-1 == line.indexOf("tid=") || -1 == line.indexOf("nid=0x"))
            {
                continue;
            }

            String name = StatTool.getObjStr(line, "\"", "\"");
            String tidStr = StatTool.getObjStr(line, "nid=0x", " ");

            if (null != name &&  null != tidStr)
            {
                try
                {
                    pid2NameMap.put(Integer.parseInt(tidStr, 16), name);
                }
                catch (NumberFormatException e)
                {
                    System.out.println(e.getMessage());
                    continue;
                }
            }
        }

        return pid2NameMap;
    }

    public int getPeriod()
    {
        return period;
    }
}

