package com.antzuhl.chat.controller;

import com.antzuhl.chat.common.HttpHeaderConstant;
import com.antzuhl.chat.utils.Record2svgUtil;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import javax.xml.soap.MimeHeader;

@Controller
@RequestMapping("/chat-room")
public class ChatController {

    @RequestMapping("/{room}")
    @ResponseBody
    public String chatRoom(String room, HttpServletResponse response) {
        response.setContentType(HttpHeaderConstant.CONTENT_TYPE_SVG);
        response.setHeader(HttpHeaderConstant.HEADER_CACHE_CONTROL, "no-cache");
        // TODO query roomName message
        return Record2svgUtil.record2svg(room);
    }
}
