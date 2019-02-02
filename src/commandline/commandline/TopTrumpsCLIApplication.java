package commandline;
//import commandline.ReadInFile;
import java.io.*;
import java.util.ArrayList; 
import java.util.Random; 
import java.util.Collections;
import java.util.Scanner; 
import java.util.InputMismatchException;


/**
 * Top Trumps command line application
 */
public class TopTrumpsCLIApplication {
   
         
	/**
	 * This main method is called by TopTrumps.java when the user specifies that they want to run in
	 * command line mode. The contents of args[0] is whether we should write game logs to a file.
 	 * @param args
	 * @throws IOException 
	 */
   public static void main(String[] args) throws IOException {
      //Test Log is launched from here :
      boolean writeGameLogsToFile = false; // Should we write game logs to file?
      
      //if (args[0].equalsIgnoreCase("true")) writeGameLogsToFile=true; // Command line selection
      //{
         //enter logic to run test log class 
      //}
   	
      boolean userWantsToQuit = false; // flag to check whether the user wants to quit the application
   	
   	// Loop until the user wants to exit the game
      while (!userWantsToQuit) {
         //variables
         //ArrayList<String> users = new ArrayList<String>(); 
        
         
         System.out.println("Game Start"); 
         ArrayList<Card> deck = new ArrayList<Card>(); 
         int roundCount = 1; 
         ArrayList<String> fileOutput = new ArrayList<>();
         PrintWriter writer = new PrintWriter("toptrumps.log", "UTF-8");
         
          
      	// ----------------------------------------------------
      	// Add your game logic here based on the requirements
      	// ----------------------------------------------------
      
         
         // 1. Read in file and load information for cards 
         File file = new File("StarCitizenDeck.txt"); 
         ImportDeckInformation fI = new ImportDeckInformation(file); 
         fileOutput.add("NEW GAME \nContents of new deck:" + fI.getDeck().toString().replace("[", "").replace("]", "")
            + "\n--------------------\n"); 
         
         // 2. Return a shuffled Deck 
         deck = fI.getShuffledDeck();
         fileOutput.add("\nShuffled Deck: " + deck.toString().replace("[", "").replace("]", "")
            +  "\n--------------------\n"); 
        
         
         // 3. Create Players and divide deck between players
         ArrayList<Card> playerDeck = new ArrayList<>(deck.subList(0,8));
         HumanPlayer user = new HumanPlayer(playerDeck, "Player You"); 
         fileOutput.add("\n" + user.getName() + "'s Deck:" + playerDeck.toString() 
            + "\n--------------------\n");
         
         playerDeck = new ArrayList<>(deck.subList(8,16));
         AIPlayer bot1 = new AIPlayer(playerDeck, "AI Player 1");
         fileOutput.add("\n" + bot1.getName() + "'s Deck:" + playerDeck.toString() 
            + "\n--------------------\n");
            
         playerDeck = new ArrayList<>(deck.subList(16,24));
         AIPlayer bot2 = new AIPlayer(playerDeck, "AI Player 2");
         fileOutput.add("\n" + bot2.getName() + "'s Deck:" + playerDeck.toString() 
            + "\n--------------------\n");
            
         playerDeck = new ArrayList<>(deck.subList(24,32));
         AIPlayer bot3 = new AIPlayer(playerDeck, "AI Player 3");
         fileOutput.add("\n" + bot3.getName() + "'s Deck:" + playerDeck.toString() 
            + "\n--------------------\n");
            
         playerDeck = new ArrayList<>(deck.subList(32,40));
         AIPlayer bot4 = new AIPlayer(playerDeck, "AI Player 4"); 
         fileOutput.add("\n" + bot4.getName() + "'s Deck:" + playerDeck.toString() 
            + "\n--------------------\n");
         
         //System.out.print("Bot2's Deck \n" + bot2.printDeck()); 
         //System.out.println("Users's Deck \n" + user.printDeck()); 
         
         //System.out.println("Selected card: " + bot3.selectCard());
         
         // 4. Start new game
         ArrayList<Player> playerList = new ArrayList<>(); 
         playerList.add(user);
         playerList.add(bot1); 
         playerList.add(bot2); 
         playerList.add(bot3); 
         playerList.add(bot4); 
         
         Scanner scan = new Scanner(System.in); 
         String activePlayer = "";
         
         while(!userWantsToQuit) {
          //File stats
         
         //Start New Round
           //Randomly selects first player during first round. 
            if(roundCount == 1) {
               Collections.shuffle(playerList);
               activePlayer = playerList.get(0).getName();
            }
         
         
            System.out.println("Round " + roundCount);
            //get all chosen cards from players and make an arraylist of cards
            ArrayList<Card> drawPile = new ArrayList<>(); 
            ArrayList<Card> commonPile = new ArrayList<>(); 
            
            // for(int i = 0; i < playersList.size() -1; i++ ) {
         //       drawPile.add((playerList.get(i)).drawTopCard());
         //    }
            
            drawPile.add((playerList.get(0)).drawTopCard()); //first player adds card
            drawPile.add((playerList.get(1)).drawTopCard()); 
            drawPile.add((playerList.get(2)).drawTopCard()); 
            drawPile.add((playerList.get(3)).drawTopCard());
            drawPile.add((playerList.get(4)).drawTopCard());
                        
            fileOutput.add("\nCards in play this round:" + drawPile.toString()
               + "\n--------------------"); 
            
            System.out.println("Round " + roundCount + ": Players have drawn their cards");
            System.out.println("You drew: " + user.drawTopCard());
            System.out.println("There are '" + user.numOfCards() + " cards in your deck"); 
            
          
            
         
            int userChoice = 0;
            if(activePlayer.equalsIgnoreCase("Player You")){
               System.out.print("It is your turn to select a category, " 
                  + "the categories are: "
                  + "\n\t1: " + (deck.get(0).getANames())[1]
                  + "\n\t2: " + (deck.get(0).getANames())[2]
                  + "\n\t3: " + (deck.get(0).getANames())[3]
                  + "\n\t4: " + (deck.get(0).getANames())[4]
                  + "\n\t5: " + (deck.get(0).getANames())[5]
                  + "\nEnter the number for your attribute: ");
               try{
                  userChoice = scan.nextInt(); //add throw for Inputmismatch exception 
               }
               catch (InputMismatchException e) {
                  System.out.print("\nPlease enter number 1 - 5");
               }
               
               
               // fileOutput.add("\nPlayer category, " 
                  //      + "the categories are: "
                  //      + "\n\t1: " + (deck.get(0).getANames())[1]
                  //      + "\n\t2: " + (deck.get(0).getANames())[2]
                  //      + "\n\t3: " + (deck.get(0).getANames())[3]
                  //      + "\n\t4: " + (deck.get(0).getANames())[4]
                  //      + "\n\t5: " + (deck.get(0).getANames())[5]
                  //      );
            //    
            //    
               // fileOutput.add("USER CHOICE: "+ userChoice);
               
            } else { //Method for bots to choose category
               Random math = new Random();
               userChoice = math.nextInt((5 - 1) + 1) + 1;
            }
         
            int catChoice = 0;
            switch(userChoice) {
               case 1 : catChoice = 1; 
                  break; 
               case 2 : catChoice = 2; 
                  break; 
               case 3 : catChoice = 3; 
                  break; 
               case 4 : catChoice = 4; 
                  break; 
               case 5 : catChoice = 5; 
                  break;
               default : 
                  {
                     fileOutput.add("No category chosen");
                     System.out.println("No category chosen");  
                  }
            }
            //System.out.println("catchoice is " + catChoice); 
            //Category selected and corresponding values when a user or computer selects a category
            fileOutput.add("\nCatergory selected: " + user.drawTopCard().getAName(catChoice) 
               + "\nCorresponding Values: "
               + "\n" + (playerList.get(0)).getName() + ": " + drawPile.get(0).getStats(catChoice) 
               + "\n" + (playerList.get(1)).getName() + ": " + drawPile.get(1).getStats(catChoice)
               + "\n" + (playerList.get(2)).getName() + ": " + drawPile.get(2).getStats(catChoice)
               + "\n" + (playerList.get(3)).getName() + ": " + drawPile.get(3).getStats(catChoice)
               + "\n" + (playerList.get(4)).getName() + ": " + drawPile.get(4).getStats(catChoice) 
               + "\n--------------------");
         
            System.out.println("PlayerList size is " + playerList.size());
            
            //Compare all cards in chosen category 
            int currentWinner = 0;
            boolean draw = false;
            for(int i = 0; i < playerList.size()-1; i++) {
               //System.out.println("current: " + drawPile.get(currentWinner).getStats(catChoice));
               //System.out.println("challenger: " + drawPile.get(i+1).getStats(catChoice));
               if(drawPile.get(currentWinner).getStats(catChoice) < drawPile.get(i+1).getStats(catChoice)) {
                  currentWinner = i+1;
                  draw = false; 
               } else if (drawPile.get(currentWinner).getStats(catChoice) == drawPile.get(i+1).getStats(catChoice)) {
                  draw = true; //drawn
                  
               }
               //System.out.println("Current winner: " + currentWinner + " " ); 
               //fileOutput.add("Current winner: " + currentWinner + " " ); 
            } 
            
             
            //return the winner --lose/draw/win
            if(draw) {
               System.out.println("Round " + roundCount + ": " 
                  + "This round was a Draw"); 
               commonPile.addAll(drawPile); 
               drawPile.clear(); 
            }
            else {
               System.out.println("Round " + roundCount + ": " 
                  + "Player " + playerList.get(currentWinner).getName() 
                  + " won this round.");
               activePlayer = playerList.get(currentWinner).getName(); 
            
               //winner gets all of common pile cards and then remove all from drawPile
               (playerList.get(currentWinner)).addCards(drawPile);  
               commonPile.clear();
               //print winning card with selected category with an arrow
               System.out.print("The winning card was "); 
               //System.out.println(drawPile.get(currentWinner));
               drawPile.clear();
               System.out.println(playerList.get(currentWinner).drawTopCard());
               
               //fileOutput.add("\n" + playerList.get(currentWinner).drawTopCard());
            }
            
            //remove cards at element 0 
            user.removeCard(0);
            bot1.removeCard(0);  
            bot2.removeCard(0);
            bot3.removeCard(0);
            bot4.removeCard(0);
          
            
            System.out.println("\nWould you like to quit? (Y/N)?"); 
            String choice = scan.next(); 
            if(choice.equalsIgnoreCase("Y")) {
               userWantsToQuit=true; 
               // use this when the user wants to exit the game
            }
           
            roundCount++; 
            
            
            //remove players from game who no longer have cards
            for(int i = 0; i < playerList.size()-1; i++) {
               if((playerList.get(i)).numOfCards() == 0) {
                  playerList.remove(i);
               }
            }
         
         
           //if only one player remains, they are the winner
            if(playerList.size() == 1) {
               System.out.println("The winner is " + playerList.get(0)); 
               userWantsToQuit = true; 
            }
            
            
         }
         
       
         
         //print all of string array at once 
         writer.println(fileOutput.toString().replace("[", "").replace("]", "").replace(",","")); 
         writer.close();
      }
   
      
   
   
   }
   
   
   

}