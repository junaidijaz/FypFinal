package com.example.Saad.MyFYPProject.models.orders;

public class Orders {

        private float id;
        private String user_id;
        private String address;
        private String cart;
        private String grand_total;
        private String order_status;
        private String created_at;
        private String updated_at;


        // Getter Methods

        public float getId() {
            return id;
        }

        public String getUser_id() {
            return user_id;
        }

        public String getAddress() {
            return address;
        }

        public String getCart() {
            return cart;
        }

        public String getGrand_total() {
            return grand_total;
        }

        public String getOrder_status() {
            return order_status;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        // Setter Methods

        public void setId(float id) {
            this.id = id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public void setCart(String cart) {
            this.cart = cart;
        }

        public void setGrand_total(String grand_total) {
            this.grand_total = grand_total;
        }

        public void setOrder_status(String order_status) {
            this.order_status = order_status;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

}
