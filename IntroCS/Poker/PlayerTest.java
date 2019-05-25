public class PlayerTest {
	public static void main(String[] args) {
		Player p1 = new Player(250.00);
		p1.deal(new Card(6, Card.SPADES));
		p1.deal(new Card(6, Card.HEARTS));
		p1.deal(new Card(Card.ACE, Card.DIAMONDS));
		p1.deal(new Card(Card.QUEEN, Card.HEARTS));
		// p1.deal(new Card(3, Card.SPADES));

		System.out.println("Your hand:");
		(p1.showHand()).printHand();
		System.out.println("Current balance: $" + p1.getBalance());

		System.out.println();
		System.out.println("*********************************************");
		System.out.println();


		System.out.println("Testing wager:");
		System.out.println("Your wager: $" + p1.wager(50.00));
		System.out.println("Current balance: $" + p1.getBalance());
		System.out.println("Your hand:");
		(p1.showHand()).printHand();
		System.out.println("Current balance: $" + p1.getBalance());

		System.out.println();
		System.out.println("*********************************************");
		System.out.println();


		System.out.println("Testing discard:");
		Card[] discardTest = p1.discard();
		System.out.println("Printing array of discarded cards:");
		for(int i = 0; i < discardTest.length; i++) {
			System.out.println(discardTest[i].toString());

		}
		System.out.println("Your hand:");
		(p1.showHand()).printHand();
		System.out.println("Current balance: $" + p1.getBalance());

		System.out.println();
		System.out.println("*********************************************");
		System.out.println();
		

		System.out.println("Testing winnings:");
		p1.winnings(20.00);
		System.out.println("Your hand: (should be reset)");
		(p1.showHand()).printHand();
		System.out.println("Current balance: $" + p1.getBalance());

	}
}