package com.ten951.design;

import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;

import java.util.*;

/**
 * 1 ,2, 3, 4, 5, 6, 7
 * 1
 * 2     5
 * 3   4   6  7
 *
 * @author 王永天
 * @date 2020-12-04 20:32
 */
public class Test1 {
    private static class TreeNode {
        private TreeNode left;
        private TreeNode right;

        private Integer val;


        public TreeNode(TreeNode left, TreeNode right, Integer val) {
            this.left = left;
            this.right = right;
            this.val = val;
        }
    }

    public List<Integer> proOrder(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        if (root == null) {
            return res;
        }
        Stack<TreeNode> stack = new Stack<>();
        stack.add(root);
        while (!stack.isEmpty()) {
            TreeNode poll = stack.pop();
            res.add(poll.val);
            if (poll.right != null) {
                stack.push(poll.right);
            }
            if (poll.left != null) {
                stack.push(poll.left);
            }
        }
        return res;
    }

    public List<Integer> inOrder(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        if (root == null) {
            return res;
        }
        TreeNode curr = root;
        Stack<TreeNode> stack = new Stack<>();
        while (!stack.isEmpty() || curr != null) {
            while (curr != null) {
                stack.push(curr);
                curr = curr.left;
            }
            TreeNode pop = stack.pop();
            res.add(pop.val);
            if (pop.right != null) {
                curr = pop.right;
            }
        }
        return res;
    }

    public List<Integer> postOrder(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        if (root == null) {
            return res;
        }
        Stack<TreeNode> stack = new Stack<>();
        Stack<TreeNode> stack2 = new Stack<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode pop = stack.pop();
            stack2.push(pop);
            if (pop.left != null) {
                stack.push(pop.left);
            }
            if (pop.right != null) {
                stack.push(pop.right);
            }
        }
        while (!stack2.isEmpty()) {
            res.add(stack2.pop().val);
        }
        return res;
    }

    public static void main(String[] args) {
        TreeNode tr3 = new TreeNode(null, null, 3);
        TreeNode tr4 = new TreeNode(null, null, 4);
        TreeNode tr6 = new TreeNode(null, null, 6);
        TreeNode tr7 = new TreeNode(null, null, 7);


        TreeNode tr2 = new TreeNode(tr3, tr4, 2);
        TreeNode tr5 = new TreeNode(tr6, tr7, 5);
        TreeNode tr1 = new TreeNode(tr2, tr5, 1);

        List<Integer> test = new Test1().postOrder(tr1);
        System.out.println("test = " + test);
    }


}
