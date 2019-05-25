public class Player{

	//declare your fields here
	private Hand hand;
	private double balance;

	//initialize your fields in the constructor
	public Player(double balance){
		this.hand = new Hand();
		this.balance = balance;
	}

	public void deal(Card c){
		(this.showHand()).addCard(c);
	}

	//Returns an array of Cards that the Player wishes to discard.
	//The game engine will call deal() on this player for each card
	//that exists in the return value. MS2 Instructions: Print the hand to
	//the terminal using System.out.println and ask the user which cards
	//they would like to discard. The user will first the number of cards they
    //wish to discard, followed by the indices, one at a time, of
	//the card(s) they would like to discard,
	//Return an array with the appropriate Card objects
	//that have been discarded, and remove the Card objects from this
	//player's hand. Use IO.readInt() for all inputs. In cases of error
	//re-ask the user for input.
	//
	// Example call to discard():
	//
	// This is your hand:
	// 0: Ace of Hearts
	// 1: 2 of Diamonds
	// 2: 5 of Hearts
	// 3: Jack of Spades
	// 4: Ace of Clubs
	// How many cards would you like to discard?
	// 2
	// 1
	// 2
	//
	// The resultant array will contain the 2 of Diamonds and the 5 of hearts in that order
	// This player's hand will now only have 3 cards
	public Card[] discard(){
		int numDiscarded;
		// If this hand has a really good hand, maybe don't discard
		if((this.showHand()).hasStraight() && (this.showHand()).hasFlush()) {   // Straight flush
	    	numDiscarded = 0;
	    } else if((this.showHand()).hasFourOfAKind()) {            				// Four of a kind
	      	numDiscarded = 0;
	    } else if((this.showHand()).hasFullHouse()) {            			  	// Full house
	      	numDiscarded = 0;
	    } else if((this.showHand()).hasFlush()) {                  				// Flush
	    	numDiscarded = 0;
	    } else if((this.showHand()).hasStraight()) {               				// Straight
	      	numDiscarded = 0;
	    } else if((this.showHand()).hasTriplet()) {                				// Three of a kind
	      	numDiscarded = 0;
	    } else if((this.showHand()).numPairs() == 2) {             				// Two pair
	    	numDiscarded = 0;
	    } else if((this.showHand()).numPairs() == 1) {             				// One pair
	     	numDiscarded = 3;
	    } else {                                      							// High card (any hand that doesn't fit above hand categories)
	    	numDiscarded = 5;
	    }
		Card[] discardedCards = new Card[numDiscarded];
		if(numDiscarded == 0) {
			return discardedCards;
		}
		for(int j = 0; j < numDiscarded; j++) {
			int indexToDiscard;
			do {
				/* if((this.showHand()).numPairs() == 1) {
					for(int i = 0; i < 5; i++) {
						if((this.showHand()).getCard(i) != null && (((this.showHand()).getCard(i)).getValue() != ((this.showHand()).highestDuplicate()).getValue())) {
							indexToDiscard = i;
						}
					}
				} else {
					indexToDiscard = (int)(Math.random() * 5);
				} */
				indexToDiscard = (int)(Math.random() * 5);
	      	} while((this.showHand()).getCard(indexToDiscard) == null);
			discardedCards[j] = (this.showHand()).getCard(indexToDiscard);
			(this.showHand()).removeCard(indexToDiscard);
		}
		return discardedCards;
	}

	//Returns the amount that this player would like to wager, returns
	//-1.0 to fold hand. Any non zero wager should immediately be deducted
	//from this player's balance. This player's balance can never be below
	// 0 at any time. This player's wager must be >= to the parameter min
	// MS2 Instructions: Show the user the minimum bet via the terminal
	//(System.out.println), and ask the user for their wager. Use
	//IO.readDouble() for input. In cases of error re-ask the user for
	//input.
	//
	// Example call to wager()
	//
	// How much do you want to wager?
	// 200
	//
	// This will result in this player's balance reduced by 200

	// Wager based on calculated probabilities of getting different hands

	public double wager(double min){
		if(this.getBalance() < min) {
			return -1.0;
		} else {
			// Bet higher if you have a better hand & vice versa
			if((this.showHand()).hasStraight() && (this.showHand()).hasFlush()) {   // Straight flush
		    	return (1.0 * (this.getBalance() - min)) + min;
		    } else if((this.showHand()).hasFourOfAKind()) {            				// Four of a kind
		      	return (0.9995 * (this.getBalance() - min)) + min;
		    } else if((this.showHand()).hasFullHouse()) {            			  	// Full house
		      	return (0.9966 * (this.getBalance() - min)) + min;
		    } else if((this.showHand()).hasFlush()) {                  				// Flush
		    	return (0.9926 * (this.getBalance() - min)) + min;
		    } else if((this.showHand()).hasStraight()) {               				// Straight
		    	return (0.9676 * (this.getBalance() - min)) + min;
		    } else if((this.showHand()).hasTriplet()) {                				// Three of a kind
		    	return (0.9425 * (this.getBalance() - min)) + min;
		    } else if((this.showHand()).numPairs() == 2) {             				// Two pair
		    	return (0.8475 * (this.getBalance() - min)) + min;
		    } else if((this.showHand()).numPairs() == 1) {             				// One pair
		     	return (0.0024 * (this.getBalance() - min)) + min;
		    } else {                                      							// High card (any hand that doesn't fit above hand categories)
		    	return (0.0 * (this.getBalance() - min)) + min;
		    }
		}
	}

	//Returns this player's hand
	public Hand showHand(){
		return this.hand;
	}

	//Returns this player's current balance
	public double getBalance(){
		return this.balance;
	}

	//Increase player's balance by the amount specified in the parameter,
	//then reset player's hand in preparation for next round. Amount will
	//be 0 if player has lost hand
	public void winnings(double amount){
		this.balance += amount;
		(this.showHand()).clear();
	}

}
