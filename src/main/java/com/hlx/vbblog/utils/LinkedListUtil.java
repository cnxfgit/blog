package com.hlx.vbblog.utils;

import com.hlx.vbblog.model.Comment;
import com.hlx.vbblog.model.Message;

import java.util.ArrayList;
import java.util.List;

public class LinkedListUtil {
    public static List<Comment> toCommentLinkedList(List<Comment> rootList, List<Comment> commentList) {
        ArrayList<Comment> linkedList = new ArrayList<>();
        for (Comment parent : rootList) {
            List<Comment> children = new ArrayList<>();
            findCommentChildren(children, parent, commentList);
            parent.setChildren(children);
            linkedList.add(parent);
        }
        return linkedList;
    }

    private static void findCommentChildren(List<Comment> children, Comment parent, List<Comment> commentList) {
        for (Comment child : commentList) {
            if (parent.getId().equals(child.getPid())) {
                children.add(child);
                findCommentChildren(children, child, commentList);
            }
        }
    }

    public static List<Message> toMessageLinkedList(List<Message> rootList, List<Message> messageList) {
        ArrayList<Message> linkedList = new ArrayList<>();
        for (Message parent : rootList) {
            List<Message> children = new ArrayList<>();
            findMessageChildren(children, parent, messageList);
            parent.setChildren(children);
            linkedList.add(parent);
        }
        return linkedList;
    }

    private static void findMessageChildren(List<Message> children, Message parent, List<Message> messageList) {
        for (Message child : messageList) {
            if (parent.getId().equals(child.getPid())) {
                children.add(child);
                findMessageChildren(children, child, messageList);
            }
        }
    }
}
