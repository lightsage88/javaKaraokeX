package com.teamtreehouse;

import com.teamtreehouse.model.Song;
import com.teamtreehouse.model.SongBook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class KaraokeMachine {
    private SongBook mSongBook;
    private BufferedReader mReader;
    private Queue<Song> mSongQueue;

    private Map<String, String> mMenu;

    public KaraokeMachine(SongBook songBook) {
        mSongBook = songBook;

        //reads text from a character input stream, buffering chars to provide for
        //efficient reading of chars, arrays and lines.
        //each read-request of a Reader(?) causes a corresponding read request to made
        // of the underlying char or byte stream
        //thus it's advisable to to wrap a buffered reader around any reader whose read() operations
        //may be costly like FileReaders and InputStreamReaders
        //ex: BufferedReader in = new BufferedReader(new FileReader("foo.in"));
        //You see that down here with the BufferedReader declared as a property on line 13.
        //then we define the value below where in place of 'in' our BufferedReader is 'mReader'
        //then we WRAP the BufferedReader AROUND the InputReaderStream :) yay yippy skippy
        mReader = new BufferedReader(new InputStreamReader(System.in));
        //ArrayDeque is an "Array Double Ended Queue" pronounced as an ArrayDeck.
        //It's a growable array allosing to add or remove an element from both sides.
        //it's the blade the daywalker of stacks/queues. It does LIFO and FIFO
        mSongQueue = new ArrayDeque<Song>();
        mMenu = new HashMap<String, String>();
        mMenu.put("add", "Add a new Song to the song book");
        mMenu.put("play", "Play next song in the queue");
        mMenu.put("choose", "Choose a song to sing!");
        mMenu.put("quit", "Give up. Exit the program");
    }

    private String promptAction() throws IOException {
        System.out.printf("There are %d songs available and %d in the queue. Your options are %n",
                mSongBook.getSongCount(),
                mSongQueue.size());
        System.out.println("dunno whtats up" + mMenu.entrySet());
        System.out.println("If I'm right, then above this will be a print out of the k, v pairs of mMenu");

        for(Map.Entry<String, String> option : mMenu.entrySet()) {
            System.out.printf("%s - %s %n",
                            option.getKey(),
                            option.getValue());
        }

        System.out.print("What do you wanna do?:  ");
        String choice = mReader.readLine();
        return choice.trim().toLowerCase();
    }

    public void run() {
        String choice = "";
        do {
            try{
                choice = promptAction();
                switch(choice) {
                    case "add":
                        Song song = promptNewSong();
                        mSongBook.addSong(song);
                        System.out.printf("%s added! %n%n", song);
                        break;
                    case "choose":
                        String artist = promptArtist();
                        Song artistSong = promptSongForArtist(artist);
                        mSongQueue.add(artistSong);
                        System.out.printf("You chose: %s %n", artistSong);
                        break;
                    case "play":
                        playNext();
                        break;
                    case "quit":
                        System.out.println("Thanks for playing, yo");
                        break;
                    default:
                        System.out.printf("Unknown choice: '%s'. Try again %n%n%n", choice);
                }
            } catch(IOException ioe) {
                System.out.println("Problem with input ioe in run");
                ioe.printStackTrace();
            }
        } while(!choice.equals("quit"));
    }

    public Song promptNewSong() throws IOException {
        //difference between print and println is that
        //println prints the string '...' and then puts the cursor on a new line like this:
        //...
        // | <- pretend that's your cursor.
        //print on the other hand prints out your string '...' and keeps the cursor on the same line:
        // ... | <- pretend that's your cursor.
        //So below it makes sense for us to use .print because right afterwards, we're expecting
        //mReader.readLine() to capture text on the same line as the printed message.
        //I've grouped these into couplets
        //one couplet
        System.out.print("Enter the artist's name:  ");
        String artist = mReader.readLine();
        //two couplet
        System.out.print("Enter the title:  ");
        String title = mReader.readLine();
        //three couplet
        System.out.print("Enter the video URL: ");
        String videoUrl = mReader.readLine();
        //then we return the new Song type object that is promised in the start
        //of the method declaration
        return new Song(artist, title, videoUrl);
    }

    private String promptArtist() throws IOException {
        System.out.println("Available artists: ");
        //artist is a list of String types. it will equal a new ArrayList made of
        // the result of mSongBook.getArtists()
        //ArrayList implements a List with List being an interface
        //https://docs.oracle.com/javase/8/docs/api/java/util/List.html
        List<String> artists = new ArrayList<>(mSongBook.getArtists());
        //this method, yet undefined, gets us an index from the artists-ARrayList<String> above
        int index = promptForIndex(artists);
        //then we return a string from returning the value of artists[index] below
        return artists.get(index);
    }

    private Song promptSongForArtist(String artist) throws IOException {
        //Some method (getSongsForArtist[arg of 'artist']) happens in songs (a list of Song type objects)
        // this list of Song type objects is yielded by mSongBook.getSongsForArtist(artist) being called
        //lets remember right now that mSongBook, of type SongBook, will have a method called getSongsForArtist which
        //accepts a string of the appropriate artist. This will yield a list of Song types.
        List<Song> songs = mSongBook.getSongsForArtist(artist);

        List<String> songTitles = new ArrayList<>();
        for(Song song : songs) {
            songTitles.add(song.getTitle());
        }
        System.out.printf("Available songs for %s %n", artist);
        int index = promptForIndex(songTitles);
        return songs.get(index);
    }

    private int promptForIndex(List<String> options) throws IOException {
        int counter = 1;
        for(String option : options) {
            System.out.printf("%d.) %s %n", counter, option);
            counter++;
        }
        System.out.print("Your choice:  ");
        String optionAsString = mReader.readLine();
        int choice = Integer.parseInt(optionAsString.trim());
        return choice - 1;
    }

    public void playNext() {
        //mSongQueue is a class property that is itself a Queue (in this case it holds Song type objects)
        //a Queue is a collection designed for holding elements prior to processing. Besides basic Collection
        //operations, queues provide extra insertion, extraction and inspection operations. They each have two forms:
        //One form throws an exception if the operation fails.
        //The other form returns a special value (either null or false) depending on the operation.
        //The remove() and poll() methods remove and return the head of the queue. What element is returned
        //depends on the queue's ordering policy. The remove() and poll() methods differ only when the queue is empty.
        //remove() -> with an empty queue throws an exception
        //poll() -> with an empty queue returns null
        Song song = mSongQueue.poll();
        if(song == null) {
            System.out.println("Sorry there are no songs in the queue." +
                    "  Use choose from the menu to add some");
        } else {
            System.out.printf("%n%n%n Open %s to hear %s by %s %n%n%n",
                    song.getVideoUrl(),
                    song.getTitle(),
                    song.getArtist());
        }
    }
}
