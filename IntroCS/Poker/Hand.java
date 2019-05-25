
/**
 * An object of type Hand represents a hand of cards.  The
 * cards belong to the class Card.  A hand is empty when it
 * is created, and any number of cards can be added to it.
 */

import java.util.ArrayList;

public class Hand {

  private Card[] hand;   // The cards in the hand.
  private int count; 
   
   /**
    * Create a hand that is initially empty.
    */
  public Hand() {
    hand = new Card[5];
	  count = 0;
  }
   
  /**
  * Remove all cards from the hand, leaving it empty.
  */
  public void clear() {
    for(int i=0 ; i<hand.length; i++){ hand[i] = null; }
	  count = 0;
  }
   
  /**
  * Add a card to the hand.  It is added at the end of the current hand.
  * @param c the non-null card to be added.
  * @throws NullPointerException if the parameter c is null.
  */
  public void addCard(Card c) {
      
	  for(int i=0 ; i<hand.length; i++){ 
		if (hand[i] == null){
			hand[i] = c;
			count = count + 1;
			break;
		}
	 }

	  
   }
   
   /**
    * Remove a card from the hand, if present.
    * @param c the card to be removed.  If c is null or if the card is not in 
    * the hand, then nothing is done.
    */
   public void removeCard(Card c) {

	for(int i=0 ; i<hand.length; i++){ 
		if (hand[i].equals(c)){
			hand[i] = null;
			count = count-1;
		}
	}

   }
   
   /**
    * Remove the card in a specified position from the hand.
    * @param position the position of the card that is to be removed, where
    * positions are starting from zero.
    * @throws IllegalArgumentException if the position does not exist in
    * the hand, that is if the position is less than 0 or greater than
    * or equal to the number of cards in the hand.
    */
   public void removeCard(int position) {
      if (position < 0 || position >= hand.length)
         throw new IllegalArgumentException("Position does not exist in hand: "
               + position);
      hand[position] = null;
      count = count-1;
   }

   /**
    * Returns the number of cards in the hand.
    */
   public int getCardCount() {
      return count;
   }
   
   /**
    * Gets the card in a specified position in the hand.  (Note that this card
    * is not removed from the hand!)
    * @param position the position of the card that is to be returned
    * @throws IllegalArgumentException if position does not exist in the hand
    */
   public Card getCard(int position) {
      if (position < 0 || position >= hand.length)
         throw new IllegalArgumentException("Position does not exist in hand: "
               + position);
       return hand[position];
   }
   
   /**
    * Sorts the cards in the hand so that cards of the same suit are
    * grouped together, and within a suit the cards are sorted by value.
    * Note that aces are considered to have the lowest value, 1.
    */
   public void sortBySuit() {
	  int size = count;
	  int nonnull = 0;
	  int index = 0;
	  
      Card[] newHand = new Card[5];
      while (size > 0) {
		 if (hand[nonnull] == null) { nonnull = nonnull+1; continue;}
         int pos = nonnull;  // Position of minimal card.
         Card c = hand[nonnull];  // Minimal card.
		 
         for (int i = nonnull+1; i < hand.length; i++) {
            Card c1 = hand[i];
			if (c1 != null){
				if ( c1.getSuit() < c.getSuit() ||
						(c1.getSuit() == c.getSuit() && c1.getValue() < c.getValue()) ) {
					pos = i;
					c = c1;
				}
			}
         }
         hand[pos] = null;
		 size = size - 1;
         newHand[index++] = c;
		 nonnull = 0;
      }
      hand = newHand;
   }
   
   /**
    * Sorts the cards in the hand so that cards of the same value are
    * grouped together.  Cards with the same value are sorted by suit.
    * Note that aces are considered to have the lowest value, 1.
    */
   public void sortByValue() {
	  int size = count;
	  int nonnull = 0;
	  int index = 0;
	  
      Card[] newHand = new Card[5];
      while (size > 0) {
		 if (hand[nonnull] == null) { nonnull = nonnull+1; continue;}
         int pos = nonnull;  // Position of minimal card.
         Card c = hand[nonnull];  // Minimal card.
		 
         for (int i = nonnull+1; i < hand.length; i++) {
            
			Card c1 = hand[i];
            if (c1 != null){
				if ( c1.getValue() < c.getValue() ||
						(c1.getValue() == c.getValue() && c1.getSuit() < c.getSuit()) ) {
					pos = i;
					c = c1;
				}
			}
         }
         hand[pos] = null;
		 size = size - 1;
         newHand[index++] = c;
		 nonnull = 0;
      }
      hand = newHand;
   }
   
   public void printHand(){
	   
	   for(int i=0; i<hand.length; i++){
		   if (hand[i] != null){
			   System.out.println(hand[i]);
		   }
	   }
	   System.out.println();
   }
   
   /******************************** Implement your methods here ****************************************/

  public int numPairs() {
    this.sortByValue();
    int i = 1;
    int count = 0;
    while(i < hand.length) {
      if(hand[i] != null) {
        if((hand[i].getValue() == hand[i-1].getValue())) {
          count++;
          i+=2;
        } else {
          i+=1;
        }
      } else {
        i+=1;
      }
    }
    return count;
  }

  public boolean hasTriplet() {
    this.sortByValue();
    for(int i = 2; i < hand.length; i++) {
      if((hand[i].getValue() == hand[i-1].getValue()) && (hand[i].getValue() == (hand[i-2]).getValue())) {
       return true;
      }
    }
    return false;
  }

  public boolean hasFlush() {
    this.sortBySuit();
    for(int i = 1; i < hand.length; i++) {
      if(hand[i].getSuit() != hand[0].getSuit()) {
        return false;
      }
    }
    return true;
  }

  public boolean hasStraight() {
    this.sortByValue();
    // Check aces high case, i.e. A 10 J Q K a.k.a 10 J Q K A
    if(hand[0].getValue() == 1 && hand[1].getValue() == 10 && hand[2].getValue() == 11 && hand[3].getValue() == 12 && hand[4].getValue() == 13) {
      return true;
    }
    // Check all other cases, including aces low case, i.e. A 2 3 4 5
    for(int i = 1; i < hand.length; i++) {
      if(hand[i].getValue() != (hand[i-1].getValue() + 1)) {
        return false;
      }
    }
    return true;
  }

  public boolean hasFullHouse() {
     this.sortByValue();
     for(int i = 2; i < hand.length; i++) {
      if((hand[i].getValue() == hand[i-1].getValue()) && (hand[i].getValue() == (hand[i-2]).getValue())) {
        if(i==2 && hand[3].getValue() == hand[4].getValue()) {
          return true;
        }
        if(i==4 && hand[0].getValue() == hand[1].getValue()) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean hasFourOfAKind() {
    this.sortByValue();
    for(int i = 3; i < hand.length; i++) {
      if((hand[i].getValue() == hand[i-1].getValue()) && (hand[i].getValue() == hand[i-2].getValue()) && (hand[i].getValue() == hand[i-3].getValue())) {
        return true;
      }
    }
    return false;
  }

  public Card highestValue() {
    Card highestCard = new Card();
    for(int i = 0; i < hand.length; i++) {
      if(hand[i] != null) {
        // If ace, return this card, b/c aces are always high
        if(hand[i].getValue() == 1) {
          return hand[i];
        }
        if(hand[i].getValue() > highestCard.getValue()) {
          highestCard = hand[i];
        }
      }
    }
    return highestCard;
  }

  public Card highestDuplicate() {
    // If no pairs or triplets, return null
    if(this.numPairs()==0 && this.hasTriplet()==false) {
      return null;
    } else { // If there are pairs or triplets, find max of pairs and/or triplets
      this.sortByValue();
      Card highestCard = new Card();
      // Find max of any pair (if no pairs, do nothing, b/c there must be a triplet)
      int i = 1;
      while(i < hand.length) {
        if(hand[i] != null) {
          if((hand[i].getValue() == hand[i-1].getValue())) {
            // If ace, return this card, b/c aces are always high
            if(hand[i].getValue() == 1) {
              return hand[i];
            }
            if(hand[i].getValue() > highestCard.getValue()) {
              highestCard = hand[i];
            }
            i+=2;
          } else {
            i+=1;
          }
        } else {
          i+=1;
        }
      }
      // Find max of any triplet (if no triplets, do nothing, b/c there must have been a pair)
      for(int j = 2; j < hand.length; j++) {
        if(hand[j] != null) {
          if((hand[j].getValue() == hand[j-1].getValue()) && (hand[j].getValue() == (hand[j-2]).getValue())) {
            // If ace, return this card, b/c aces are always high
            if(hand[j].getValue() == 1) {
              return hand[i];
            }
            if(hand[j].getValue() > highestCard.getValue()) {
              highestCard = hand[i];
            }
          }
        }
      }
      return highestCard; // Returns highest card that is a duplicate, i.e. part of either a pair or a triplet
    }
  }

  public int compareTo(Hand h) {
    // Give this (instance) hand a rank
    int rank1;
    this.sortByValue();
    if(this.hasStraight() && this.hasFlush()) {   // Straight flush
      rank1 = 8;
    } else if(this.hasFourOfAKind()) {            // Four of a kind
      rank1 = 7;
    } else if(this.hasFullHouse()) {              // Full house
      rank1 = 6;
    } else if(this.hasFlush()) {                  // Flush
      rank1 = 5;
    } else if(this.hasStraight()) {               // Straight
      rank1 = 4;
    } else if(this.hasTriplet()) {                // Three of a kind
      rank1 = 3;
    } else if(this.numPairs() == 2) {             // Two pair
      rank1 = 2;
    } else if(this.numPairs() == 1) {             // One pair
      rank1 = 1;
    } else {                                      // High card (any hand that doesn't fit above hand categories)
      rank1 = 0;
    }
    // Give the other (param) hand a rank
    int rank2;
    h.sortByValue();
    if(h.hasStraight() && h.hasFlush()) {  // Straight flush
      rank2 = 8;
    } else if(h.hasFourOfAKind()) {            // Four of a kind
      rank2 = 7;
    } else if(h.hasFullHouse()) {              // Full house
      rank2 = 6;
    } else if(h.hasFlush()) {                  // Flush
      rank2 = 5;
    } else if(h.hasStraight()) {               // Straight
      rank2 = 4;
    } else if(h.hasTriplet()) {                // Three of a kind
      rank2 = 3;
    } else if(h.numPairs() == 2) {             // Two pair
      rank2 = 2;
    } else if(h.numPairs() == 1) {             // One pair
      rank2 = 1;
    } else {                                       // High card (any hand that doesn't fit above hand categories)
      rank2 = 0;
    }
    // Compare hand ranks to determine winner
    if(rank1 > rank2) {
      return 1;
    } else if(rank1 < rank2) {
      return -1;
    } else {                                         
      // Straight flush: "Each straight flush is ranked by the rank of its highest-ranking card."
      if(rank1 == 8 && rank2 == 8) {                
        if((this.highestValue()).getValue() == 1 && (h.highestValue()).getValue() == 1) { // Both have aces
          if((hand[1].getValue() == 10) && (h.getCard(1)).getValue() != 10) { // if Hand 1 has a Royal Flush and the other hand doesn't
            return 1;
          } else if((hand[1].getValue() != 10) && (h.getCard(1)).getValue() == 10) { // if Hand 1 doesn't have a Royal Flush but the other hand does
            return -1;
          } else { // if both hands have royal flushes
            return 0;
          }
        } else if(((this.highestValue()).getValue() == 1 && (h.highestValue()).getValue() != 1)) { // You have an ace, they don't
          if(hand[1].getValue() == 10) { // Only way to win is if you have a royal flush
            return 1;
          } else {
            return -1;
          }
        } else if(((this.highestValue()).getValue() != 1 && (h.highestValue()).getValue() == 1)) { // They have an ace, you don't
          if((h.getCard(1)).getValue() == 10) { // Only way they win is if they have a royal flush
            return -1;
          } else {
            return 1;
          }
        } else if((this.highestValue()).getValue() > (h.highestValue()).getValue()) {
          return 1; // You win if you have a highest value greater than the other hand
        } else if((this.highestValue()).getValue() < (h.highestValue()).getValue()) {
          return -1; // You lose if you have a highest value smaller than the other hand
        } else { 
          return 0; // The only way to tie is to have exactly identical straights
        }
      }
      // Four of a kind: "Each four of a kind is ranked first by the rank of its quadruplet, and then by the rank of its kicker."
      else if(rank1 == 7 && rank2 == 7) {
        // Compare based on rank of quadruplets
        if((this.highestDuplicate()).getValue() > (h.highestDuplicate()).getValue() || ((this.highestDuplicate()).getValue() == 1 && (h.highestDuplicate()).getValue() != 1)) {
          return 1;
        } else if((this.highestDuplicate()).getValue() < (h.highestDuplicate()).getValue() || ((this.highestDuplicate()).getValue() != 1 && (h.highestDuplicate()).getValue() == 1)) {
          return -1;
        } else {
          // If Hand 1 and Hand 2 come from the same deck, they can never both have identical quadruplets, and thus we don't have to worry about each hand's kicker.
          return 0;
        }
      }
      // Full house: "Each full house is ranked first by the rank of its triplet, and then by the rank of its pair."
      else if(rank1 == 6 && rank2 == 6) {
        // Compare based on rank of triplets
        int thisTripletRank = 0;
        for(int i = 2; i < hand.length; i++) {
          if((hand[i].getValue() == hand[i-1].getValue()) && (hand[i].getValue() == (hand[i-2]).getValue())) {
            thisTripletRank = hand[i].getValue();
          }
        }
        int thatTripletRank = 0;
        for(int j = 2; j < h.getCardCount(); j++) {
          if(((h.getCard(j)).getValue() == (h.getCard(j-1)).getValue()) && ((h.getCard(j)).getValue() == (h.getCard(j-2)).getValue())) {
            thatTripletRank = (h.getCard(j)).getValue();
          }
        }
        if((thisTripletRank > thatTripletRank) || (thisTripletRank == 1 && thatTripletRank != 1)) {
          return 1;
        } else if((thisTripletRank < thatTripletRank) || (thisTripletRank != 1 && thatTripletRank == 1)) {
          return -1;
        } else {
          // If Hand 1 and Hand 2 come from the same deck, they can never both have identical triplets and thus we don't have to worry about each hand's pair.
          return 0;
        }
      }
      // Flush: "Each flush is ranked first by the rank of its highest-ranking card, then by the rank of its second highest-ranking card, then by the rank of its third highest-ranking card, then by the rank of its fourth highest-ranking card, and finally by the rank of its lowest-ranking card."
      else if(rank1 == 5 && rank2 == 5) {
        Hand tempThisHand = new Hand();
        for(int i = 0; i < hand.length; i++) {
          tempThisHand.addCard(hand[i]);
        }
        Hand tempThatHand = new Hand();
        for(int j = 0; j < h.getCardCount(); j++) {
          tempThatHand.addCard(h.getCard(j));
        }
        int nonnull = 0;
        while(tempThisHand.getCardCount() > 0) {
          if(((tempThisHand.highestValue()).getValue() > (tempThatHand.highestValue()).getValue()) || ((tempThisHand.highestValue()).getValue() == 1 && (tempThatHand.highestValue()).getValue() != 1)) {
            return 1;
          } else if((tempThisHand.highestValue()).getValue() < (tempThatHand.highestValue()).getValue() || ((tempThisHand.highestValue()).getValue() != 1 && (tempThatHand.highestValue()).getValue() == 1)) {
            return -1;
          } else {
            // Can't start the loop on a null value 
            if(hand[nonnull] == null) { 
              nonnull = nonnull+1; 
            }
            // If both hands have same highest value (incl. aces), in both hands, remove cards with value equal to the highest value
            int valueToRemove = (tempThisHand.highestValue()).getValue();
            for(int k = nonnull; k < hand.length; k++) {
              if(tempThisHand.getCard(k) != null) {
                if((tempThisHand.getCard(k)).getValue() == valueToRemove) {
                  tempThisHand.removeCard(k);
                }
              }
            }
            for(int l = nonnull; l < hand.length; l++) {
              if(tempThatHand.getCard(l) != null) {
                if((tempThatHand.getCard(l)).getValue() == valueToRemove) {
                  tempThatHand.removeCard(l);
                }
              }
            }
          }
        } 
        return 0;
      }
      // Straight: "Each straight is ranked by the rank of its highest-ranking card."
      else if(rank1 == 4 && rank2 == 4) {
        if((this.highestValue()).getValue() == 1 && (h.highestValue()).getValue() == 1) { // Both have aces
          if((hand[1].getValue() == 10) && (h.getCard(1)).getValue() != 10) { // if Hand 1 has a Royal Flush and the other hand doesn't
            return 1;
          } else if((hand[1].getValue() != 10) && (h.getCard(1)).getValue() == 10) { // if Hand 1 doesn't have a Royal Flush but the other hand does
            return -1;
          } else { // if both hands have royal flushes
            return 0;
          }
        } else if(((this.highestValue()).getValue() == 1 && (h.highestValue()).getValue() != 1)) { // You have an ace, they don't
          if(hand[1].getValue() == 10) { // Only way to win is if you have a royal flush
            return 1;
          } else {
            return -1;
          }
        } else if(((this.highestValue()).getValue() != 1 && (h.highestValue()).getValue() == 1)) { // They have an ace, you don't
          if((h.getCard(1)).getValue() == 10) { // Only way they win is if they have a royal flush
            return -1;
          } else {
            return 1;
          }
        } else if((this.highestValue()).getValue() > (h.highestValue()).getValue()) {
          return 1; // You win if you have a highest value greater than the other hand
        } else if((this.highestValue()).getValue() < (h.highestValue()).getValue()) {
          return -1; // You lose if you have a highest value smaller than the other hand
        } else { 
          return 0; // The only way to tie is to have exactly identical straights
        }
      }
      // Three of a kind: "Each three of a kind is ranked first by the rank of its triplet, then by the rank of its highest-ranking kicker, and finally by the rank of its lowest-ranking kicker."
      else if(rank1 == 3 && rank2 == 3) {
        // Compare based on rank of triplets
        int thisTripletRank = 0;
        for(int i = 2; i < hand.length; i++) {
          if((hand[i].getValue() == hand[i-1].getValue()) && (hand[i].getValue() == (hand[i-2]).getValue())) {
            thisTripletRank = hand[i].getValue();
          }
        }
        int thatTripletRank = 0;
        for(int j = 2; j < h.getCardCount(); j++) {
          if(((h.getCard(j)).getValue() == (h.getCard(j-1)).getValue()) && ((h.getCard(j)).getValue() == (h.getCard(j-2)).getValue())) {
            thatTripletRank = (h.getCard(j)).getValue();
          }
        }
        if((thisTripletRank > thatTripletRank) || (thisTripletRank == 1 && thatTripletRank != 1)) {
          return 1;
        } else if((thisTripletRank < thatTripletRank) || (thisTripletRank != 1 && thatTripletRank == 1)) {
          return -1;
        } else {
          // If Hand 1 and Hand 2 come from the same deck, they can never both have identical triplets, so we don't have to check the ranks of both hands' kickers.  
          return 0;
        }
      }
      // Two pair: "Each two pair is ranked first by the rank of its highest-ranking pair, then by the rank of its lowest-ranking pair, and finally by the rank of its kicker."
      else if(rank1 == 2 && rank2 == 2) {
        Hand tempThisHand = new Hand();
        for(int i = 0; i < hand.length; i++) {
          tempThisHand.addCard(hand[i]);
        }
        Hand tempThatHand = new Hand();
        for(int j = 0; j < h.getCardCount(); j++) {
          tempThatHand.addCard(h.getCard(j));
        }
        int nonnull = 0;
        while(tempThisHand.numPairs() > 0) {
          if(((tempThisHand.highestDuplicate()).getValue() > (tempThatHand.highestDuplicate()).getValue()) || ((tempThisHand.highestDuplicate()).getValue() == 1 && (tempThatHand.highestDuplicate()).getValue() != 1)) {
            return 1;
          } else if((tempThisHand.highestDuplicate()).getValue() < (tempThatHand.highestDuplicate()).getValue() || ((tempThisHand.highestDuplicate()).getValue() != 1 && (tempThatHand.highestDuplicate()).getValue() == 1)) {
            return -1;
          } else {
            // Can't start the loop on a null value
            if(hand[nonnull] == null) { 
              nonnull = nonnull+1; 
            }
            // If both hands have same highest pairs (incl. aces), in both hands, remove pairs with values equal to the highest value
            int valueToRemove = (tempThisHand.highestDuplicate()).getValue();
            for(int k = nonnull; k < hand.length; k++) {
              if(tempThisHand.getCard(k) != null) {
                if((tempThisHand.getCard(k)).getValue() == valueToRemove) {
                  tempThisHand.removeCard(k);
                }
              }
            }
            for(int l = nonnull; l < hand.length; l++) {
              if(tempThatHand.getCard(l) != null) {
                if((tempThatHand.getCard(l)).getValue() == valueToRemove) {
                  tempThatHand.removeCard(l);
                }
              }
            }
          }
        }
        // If the program makes it to this point, then both (temp) hands have lost both of their pairs, because their pairs were identical in value
        // Now compare kickers. Since each hand only has 1 card left, the highest value of the hand will yield the value of the kickers.
        if(((tempThisHand.highestValue()).getValue() > (tempThatHand.highestValue()).getValue()) || ((tempThisHand.highestValue()).getValue() == 1 && (tempThatHand.highestValue()).getValue() != 1)) {
          return 1;
        } else if((tempThisHand.highestValue()).getValue() < (tempThatHand.highestValue()).getValue() || ((tempThisHand.highestValue()).getValue() != 1 && (tempThatHand.highestValue()).getValue() == 1)) {
          return -1;
        } else {
          return 0;
        }
      }
      // One pair: "Each one pair is ranked first by the rank of its pair, then by the rank of its highest-ranking kicker, then by the rank of its second highest-ranking kicker, and finally by the rank of its lowest-ranking kicker."
      else if(rank1 == 1 && rank2 == 1) {
        Hand tempThisHand = new Hand();
        for(int i = 0; i < hand.length; i++) {
          tempThisHand.addCard(hand[i]);
        }
        Hand tempThatHand = new Hand();
        for(int j = 0; j < h.getCardCount(); j++) {
          tempThatHand.addCard(h.getCard(j));
        }
        if(((tempThisHand.highestDuplicate()).getValue() > (tempThatHand.highestDuplicate()).getValue()) || ((tempThisHand.highestDuplicate()).getValue() == 1 && (tempThatHand.highestDuplicate()).getValue() != 1)) {
          return 1;
        } else if((tempThisHand.highestDuplicate()).getValue() < (tempThatHand.highestDuplicate()).getValue() || ((tempThisHand.highestDuplicate()).getValue() != 1 && (tempThatHand.highestDuplicate()).getValue() == 1)) {
          return -1;
        } else {
          // If both hands have same highest pairs (incl. aces)... in both hands, remove pairs with values equal to the highest value
          int valueToRemove = (tempThisHand.highestDuplicate()).getValue();
          for(int k = 0; k < hand.length; k++) {
            if((tempThisHand.getCard(k)).getValue() == valueToRemove) {
              tempThisHand.removeCard(k);
            }
          }
          for(int l = 0; l < hand.length; l++) {
            if((tempThatHand.getCard(l)).getValue() == valueToRemove) {
              tempThatHand.removeCard(l);
            }
          }
          // Now, loop through remaining cards and: (1) compare highest-ranking kicker (2) remove highest-ranking kicker until something is returned or loop ends
          int nonnull = 0;
          while(tempThisHand.getCardCount() > 0) {
            if(((tempThisHand.highestValue()).getValue() > (tempThatHand.highestValue()).getValue()) || ((tempThisHand.highestValue()).getValue() == 1 && (tempThatHand.highestValue()).getValue() != 1)) {
              return 1;
            } else if((tempThisHand.highestValue()).getValue() < (tempThatHand.highestValue()).getValue() || ((tempThisHand.highestValue()).getValue() != 1 && (tempThatHand.highestValue()).getValue() == 1)) {
              return -1;
            } else {
              // Can't start the loop on a null value
              if(hand[nonnull] == null) { 
                nonnull = nonnull+1; 
              }
              // If both hands have same highest kicker value (incl. aces)... in both hands, remove kickers with that value
              valueToRemove = (tempThisHand.highestValue()).getValue();
              for(int m = nonnull; m < hand.length; m++) {
                if(tempThisHand.getCard(m) != null) {
                  if((tempThisHand.getCard(m)).getValue() == valueToRemove) {
                    tempThisHand.removeCard(m);
                  }
                }
              }
              for(int n = nonnull; n < hand.length; n++) {
                if(tempThatHand.getCard(n) != null) {
                  if((tempThatHand.getCard(n)).getValue() == valueToRemove) {
                    tempThatHand.removeCard(n);
                  }
                }
              }
            }
          } 
          return 0;
        }
      }
      // High card: "Each high card hand is ranked first by the rank of its highest-ranking card, then by the rank of its second highest-ranking card, then by the rank of its third highest-ranking card, then by the rank of its fourth highest-ranking card, and finally by the rank of its lowest-ranking card."
      else {
        Hand tempThisHand = new Hand();
        for(int i = 0; i < hand.length; i++) {
          tempThisHand.addCard(hand[i]);
        }
        Hand tempThatHand = new Hand();
        for(int j = 0; j < h.getCardCount(); j++) {
          tempThatHand.addCard(h.getCard(j));
        }
        int nonnull = 0;
        while(tempThisHand.getCardCount() > 0) {
          if(((tempThisHand.highestValue()).getValue() > (tempThatHand.highestValue()).getValue()) || ((tempThisHand.highestValue()).getValue() == 1 && (tempThatHand.highestValue()).getValue() != 1)) {
            return 1;
          } else if((tempThisHand.highestValue()).getValue() < (tempThatHand.highestValue()).getValue() || ((tempThisHand.highestValue()).getValue() != 1 && (tempThatHand.highestValue()).getValue() == 1)) {
            return -1;
          } else {
            // Can't start the loop on a null value
            if(hand[nonnull] == null) { 
              nonnull = nonnull+1; 
            }
            // If both hands have same highest value (incl. aces)... in both hands, remove cards with value equal to the highest value
            int valueToRemove = (tempThisHand.highestValue()).getValue();
            for(int k = nonnull; k < hand.length; k++) {
              if(tempThisHand.getCard(k) != null) {
                if((tempThisHand.getCard(k)).getValue() == valueToRemove) {
                  tempThisHand.removeCard(k);
                }
              }
            }
            for(int l = nonnull; l < hand.length; l++) {
              if(tempThatHand.getCard(l) != null) {
                if((tempThatHand.getCard(l)).getValue() == valueToRemove) {
                  tempThatHand.removeCard(l);
                }
              }
            }
          }
        } 
        return 0;
      } 
    }
  }  
}