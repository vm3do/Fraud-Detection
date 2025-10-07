package dao;

import entity.Card;
import entity.CardStatus;
import java.util.List;
import java.util.Optional;

public interface CardDAO {

    boolean save(Card card);

    Optional<Card> findById(int id);

    Optional<Card> findByNumber(String number);

    List<Card> findByClientId(int clientId);

    List<Card> findAll();

    boolean updateStatus(int cardId, CardStatus newStatus);
    
}
