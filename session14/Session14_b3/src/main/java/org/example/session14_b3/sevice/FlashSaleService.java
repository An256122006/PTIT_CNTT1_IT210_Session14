package org.example.session14_b3.sevice;


import org.example.session14_b3.model.Product;
import org.example.session14_b3.model.Order;
import org.example.session14_b3.util.HibernateUtils;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.LockAcquisitionException;

public class FlashSaleService {

    public String buyNow(Long productId){

        Session session = HibernateUtils.getFactory().openSession();
        Transaction tx = null;

        try{
            Product product = session.get(
                    Product.class, productId, LockMode.PESSIMISTIC_WRITE
            );

            if(product == null){
                throw new Exception("Sản phẩm không tồn tại");
            }

            if(product.getStock() <= 0){
                throw new Exception("Het Hang");
            }

            product.setStock(product.getStock() - 1);

            Order order = new Order();
            order.setProductId(productId);

            order.setQuantity(1);

            order.setStatus("SUCCESS");

            session.persist(order);
            tx.commit();

            return "Dat Hang Thanh Cong";


        }catch(LockAcquisitionException e){
            if(tx != null) tx.rollback();
            return "He thon dang ban, vui long thu lai";
        }catch(Exception e){
            if(tx != null) tx.rollback();
            return e.getMessage();
        }finally {
            session.close();
        }


    }
}
