# Wordle solver

## Potential word list

The word list in the `dictionary.txt` file is derived from all the 5-letter
words provided in the UNIX file `/usr/share/dict/words`. UNIX `words` can be
filtered using the following shell command:

```sh
cat /usr/share/dict/words | grep -e "^.\{5\}$" > dictionary.txt
```

This returns all possible 5-letter words in the builtin (macOS) dictionary
using a regex expression to capture exactly five (`{5}`) characters (`.`)
between the start (`^`) and end (`$`) of a line. 

Some words are proper nouns, and some may not even appear to be english at all,
so take this 10k-long list with a grain of salt. For simplicity, this list
is also converted to all lowercase.

## Wordle Rules

You have 6 guesses to determine the secret 5-letter word. Each guess must be a
valid word (exists in the dictionary).

When a guess is submitted, each letter gets validated:
- ⬛ This letter does not exist in the word in any position.
- 🟨 This letter exists in the word, but not in this position.
- 🟩 This letter exists in the word, and it is in the correct position. 

The meta-game is therefore to achieve a full green row (🟩🟩🟩🟩🟩). 

6 guesses of 5 letters each means that it is almost impossible to lose, because
by the time you finish the 5th row, you should have used the entire alphabet.

## Finding a solution

Based on the state of the previously submitted words, you can immediately rule
out several letters from the alphabet, which eliminates a significant portion
of the dictionary. You can also use the yellow letters to filter the dictionary
exclusively for words with that letter in any other position. Finally, you can
use green letters to filter exclusively for words containing that letter in
that exact position.

### Using frequency analysis
It appears to be ideal to choose a starting word that will 'knock out' the
largest quantity of words from the dictionary, based on letter frequency.
According to wikipedia, the most common letters in the english dictionary are:

Letter | Frequency
--- | ---
E | 11%
S | 8.7%
I | 8.2%
A | 7.8%
R | 7.3%

This, conveniently, spells the 5-letter word `RAISE`. 

However, based on the 5-letter subset of the UNIX `words` dictionary, we can 
calculate a better frequency table using the shell:

```sh
cat dictionary.txt | sed 's/./&\n/g' | sort | uniq -c | sort -nr
```

Notes for the above command:
- `sed` is matching every letter (`.`), then replacing it with itself plus a
  newline character (`&\n`). This produces a list of every single character.
- `sort` gathers all of the same characters together sequentially in the list
- `uniq -c` counts the frequency of each line (assuming a sorted list). 
- `sort -nr` sorts the resulting list numerically, largest first. 

The resulting frequency table is as follows (reference: total letter count is
51150):

Letter | Frequency
--- | ---
A | 11.3%
E | 9.6%
R | 7.0%
O | 6.5%
I | 6.5%
S | 5.6%
L | 5.5%
N | 5.4%
T | 5.4%
U | 4.5%

From this, the following starter words are great:
- RAISE
- ROAST
- ARIES

They will knock out a significant portion of the alphabet, and therefore a
significant portion of words. The number of words in the dictionary that do not
contain the top 5 letters is only 302, which is only 2.95% of the full
dictionary. Therefore, using the top 5 letters can potentially eliminate 97%
of the dictionary. 

### Using programmatic elimination

The meta-game here is to reduce the dictionary size to 1. Using the above
probabilities, 


## Weirdness

- The dictionary doesn't contain the word `proud`. 
- Repeated letters won't work!!!

## Architectural design

Solver
- Loops based on user input
- Feeds results into word list for filtering
- Prints new best-guesses from updated word list

GameState
- Keep track of current filtered word list.

Dictionary
- Provide default word list from file
- Provide util to calculate letter frequency based on list

