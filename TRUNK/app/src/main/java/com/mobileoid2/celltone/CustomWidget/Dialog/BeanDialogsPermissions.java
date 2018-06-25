package com.mobileoid2.celltone.CustomWidget.Dialog;

public class BeanDialogsPermissions {

        private String title;
        private String description;


        public BeanDialogsPermissions(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

    }