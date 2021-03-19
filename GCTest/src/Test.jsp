<%@ page import="java.util.Map" %>

<html>
<head>
    <title>服务器线程信息</title>
</head>
<body>
<pre>
    <%
        for (Map.Entry<Thread, StackTraceElement[]> stackTrace : Thread.getAllStackTraces().entrySet()) {
            Thread key = stackTrace.getKey();
            StackTraceElement[] value = stackTrace.getValue();
            if (key.equals(Thread.currentThread())) {
                continue;
            }
            System.out.print("\n线程" + key.getName() + "\n");
            for (StackTraceElement element : value) {
                System.out.print("\t" + element + "\n");
            }
        }
    %>
</pre>
</body>
</html>