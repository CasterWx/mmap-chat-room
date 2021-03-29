package com.antzuhl.chat.utils;

public class Record2svgUtil {

    // TODO
    public static String record2svg()  {
        return null;
    }

    public static String record2svg(String name)  {
        String svg = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<svg width=\"120\" height=\"100\" version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n" +
                "  <title>antz::record - Chat Room</title>\n" +
                "  <foreignObject width=\"100\" height=\"90\">\n" +
                "    <body xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "      <style>\n" +
                "        ::selection {\n" +
                "          background: #444;\n" +
                "          color: #fff;\n" +
                "        }\n" +
                "\n" +
                "        body {\n" +
                "          margin: 0;;\n" +
                "          padding: 0;\n" +
                "        }\n" +
                "\n" +
                "        .container {\n" +
                "          position: absolute;\n" +
                "          bottom: 0;\n" +
                "          font-size: 20px;\n" +
                "        }\n" +
                "        .container.light {\n" +
                "          bottom: auto;\n" +
                "          width: 100%;\n" +
                "          height: 100%;\n" +
                "          background: #fbfbfb;\n" +
                "          border-radius: 5px;\n" +
                "          line-height: 1.4;\n" +
                "          color: #000;\n" +
                "          padding-top: 30px;\n" +
                "          border: 1px solid #d4d4d4;\n" +
                "          box-sizing: border-box;\n" +
                "          overflow: hidden;\n" +
                "        }\n" +
                "\n" +
                "        .container.light::before {\n" +
                "          content: \"\";\n" +
                "          position: absolute;\n" +
                "          background: #fbfbfb;\n" +
                "          width: 100%;\n" +
                "          height: 26px;\n" +
                "          top: 0;\n" +
                "          left: 0;\n" +
                "          z-index: 2;\n" +
                "        }\n" +
                "\n" +
                "        .container.light::after {\n" +
                "          content: \"\";\n" +
                "          position: absolute;\n" +
                "          -webkit-border-radius: 50%;\n" +
                "          border-radius: 50%;\n" +
                "          background: #fc625d;\n" +
                "          width: 12px;\n" +
                "          height: 12px;\n" +
                "          left: 10px;\n" +
                "          margin-top: -24px;\n" +
                "          -webkit-box-shadow: 20px 0 #fdbc40, 40px 0 #35cd4b;\n" +
                "          box-shadow: 20px 0 #fdbc40, 40px 0 #35cd4b;\n" +
                "          z-index: 2;\n" +
                "        }\n" +
                "\n" +
                "        .container.light .content {\n" +
                "          position: absolute;\n" +
                "          bottom: 0;\n" +
                "          padding: 6px 8px;\n" +
                "        }\n" +
                "        .container.light .content::before {\n" +
                "          color: #21252b;\n" +
                "          content: 'dawawdaw'\n" +
                "          height: 26px;\n" +
                "          line-height: 26px;\n" +
                "          font-size: 12px;\n" +
                "          position: fixed;\n" +
                "          top: 0;\n" +
                "          left: 0;\n" +
                "          width: 100%;\n" +
                "          font-family: Ubuntu,sans-serif;\n" +
                "          font-weight: 700;\n" +
                "          padding: 0 80px;\n" +
                "          text-align: center;\n" +
                "          z-index: 2;\n" +
                "          box-sizing: border-box;\n" +
                "        }\n" +
                "\n" +
                "        .nickname {\n" +
                "          padding-right: 5px;\n" +
                "          color: brown;\n" +
                "          font-family: Consolas, Monaco, monospace;\n" +
                "          white-space: nowrap;\n" +
                "        }\n" +
                "        .name {\n" +
                "          font-weight: bold;\n" +
                "        }\n" +
                "        .uid {\n" +
                "          color: #cc1105;\n" +
                "          font-weight: bold;\n" +
                "        }\n" +
                "        .time {\n" +
                "          color: #6f6f6f;\n" +
                "        }\n" +
                "        .msg {\n" +
                "          font-family: serif;\n" +
                "          word-break: break-all;\n" +
                "        }\n" +
                "      </style>\n" +
                "      <div class=\"container light\">\n" +
                "        <div class=\"content\">\n" +
                "        dawdawda\n" +
                "        </div>\n" +
                "      </div>\n" +
                "    </body>\n" +
                "  </foreignObject>\n" +
                "</svg>";
        return svg;
    }

    public static String processUnsafeHtml(String source) {
        source = source.replace("/&lt;/g", "<").replace("&gt;/g", ">");
        return source.replace("/&/g", "&amp;").replace("/</g", "&lt;").replace("/>/g", "&gt;");
    }

}
