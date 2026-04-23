package org.example;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Transactional
public class OrdersRepository {
    private final SessionFactory sessionFactory;

    public void order(Long id ,Long walletId,double total){
        Session session=sessionFactory.getCurrentSession();
        Transaction transaction=session.beginTransaction();
        try {

            Order order = session.get(Order.class, orderId);
            order.setStatus("PAID");
            session.update(order);

            if (true) throw new RuntimeException("Kết nối đến cổng thanh toán thất bại!");
            Wallet wallet = session.get(Wallet.class, walletId);
            wallet.setBalance(wallet.getBalance() - totalAmount);
            session.update(wallet);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            System.out.println("Lỗi hệ thống: " + e.getMessage());
        }

    }


}
