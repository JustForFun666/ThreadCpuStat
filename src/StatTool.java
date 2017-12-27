import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by  on 17-12-24.
 */
public class StatTool
{
    public static List<String> execute(String cmd)
    {
        List<String> result = new ArrayList<String>(1);
        String[] cmds = {"/bin/bash", "-c", cmd};

        try
        {
            Process process = Runtime.getRuntime().exec(cmds);
            InputStream os = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(os);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null)
            {
                result.add(line);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取源串和目的串之间的字符串
     * @param srcStr 待截取的字符串
     * @param beginStr 源串
     * @param endStr 目的串
     * @return 截取后的字符串
     */
    public static String getObjStr(String srcStr, String beginStr, String endStr)
    {
        if (null == srcStr || null == beginStr || null == endStr)
        {
            return null;
        }

        int beginIndex;
        if (-1 == (beginIndex = srcStr.indexOf(beginStr)))
        {
            return null;
        }

        int endIndex;
        if (-1 == (endIndex = srcStr.indexOf(endStr, beginIndex + beginStr.length())))
        {
            return null;
        }

        return srcStr.substring(beginIndex + beginStr.length(), endIndex);
    }

    public static int getAveOfInts(List<Float> ints)
    {
        if (null == ints)
        {
            return Integer.MIN_VALUE;
        }

        int result = 0;

        for (float i : ints)
        {
            result += i;
        }
        return result/ints.size();
    }
}
