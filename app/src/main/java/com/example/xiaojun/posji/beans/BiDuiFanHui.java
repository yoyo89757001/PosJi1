package com.example.xiaojun.posji.beans;

/**
 * Created by Administrator on 2017/10/23.
 */

public class BiDuiFanHui {


    /**
     * face1 : {"rect":{"left":576,"top":589,"width":311,"height":311},"confidence":0.99959975}
     * face2 : {"rect":{"left":492,"top":754,"width":702,"height":702},"confidence":0.999997}
     * score : 96.36503
     */

    private Face1Bean face1;
    private Face2Bean face2;
    private double score;

    public Face1Bean getFace1() {
        return face1;
    }

    public void setFace1(Face1Bean face1) {
        this.face1 = face1;
    }

    public Face2Bean getFace2() {
        return face2;
    }

    public void setFace2(Face2Bean face2) {
        this.face2 = face2;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public static class Face1Bean {
        /**
         * rect : {"left":576,"top":589,"width":311,"height":311}
         * confidence : 0.99959975
         */

        private RectBean rect;
        private double confidence;

        public RectBean getRect() {
            return rect;
        }

        public void setRect(RectBean rect) {
            this.rect = rect;
        }

        public double getConfidence() {
            return confidence;
        }

        public void setConfidence(double confidence) {
            this.confidence = confidence;
        }

        public static class RectBean {
            /**
             * left : 576
             * top : 589
             * width : 311
             * height : 311
             */

            private int left;
            private int top;
            private int width;
            private int height;

            public int getLeft() {
                return left;
            }

            public void setLeft(int left) {
                this.left = left;
            }

            public int getTop() {
                return top;
            }

            public void setTop(int top) {
                this.top = top;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }
        }
    }

    public static class Face2Bean {
        /**
         * rect : {"left":492,"top":754,"width":702,"height":702}
         * confidence : 0.999997
         */

        private RectBeanX rect;
        private double confidence;

        public RectBeanX getRect() {
            return rect;
        }

        public void setRect(RectBeanX rect) {
            this.rect = rect;
        }

        public double getConfidence() {
            return confidence;
        }

        public void setConfidence(double confidence) {
            this.confidence = confidence;
        }

        public static class RectBeanX {
            /**
             * left : 492
             * top : 754
             * width : 702
             * height : 702
             */

            private int left;
            private int top;
            private int width;
            private int height;

            public int getLeft() {
                return left;
            }

            public void setLeft(int left) {
                this.left = left;
            }

            public int getTop() {
                return top;
            }

            public void setTop(int top) {
                this.top = top;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }
        }
    }
}
