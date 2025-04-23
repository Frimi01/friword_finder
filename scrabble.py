import argparse
import json
import itertools

# Parsing arguments
parser = argparse.ArgumentParser()
parser.add_argument(
    "Action", choices=["fwrd", "iswrd", "letters"], help="Choose output type"
)
parser.add_argument("Value", help="Choose output value")
parser.add_argument(
    "--MinLength", type=int, default=2, help="Minimum Length of Returned words"
)
parser.add_argument("--MaxLength", type=int, help="Maximum Length of Returned words")

args = parser.parse_args()

value = args.Value
minl = args.MinLength
maxl = args.MaxLength


# Loading words
def load_dictonary_json(filepath):
    try:
        with open(filepath) as f:
            return json.load(f)
    except FileNotFoundError:
        print("Error: words JSON file not found!")
        exit(1)


scrabble_words = load_dictonary_json("assets/words.json")


# Defining actions
def is_scrabble_word(word):
    return word.lower() in scrabble_words


def find_valid_words(letters, min_length, max_length):
    found_words = set()

    for length in range(min_length, max_length):
        for perm in itertools.permutations(letters, length):
            word = "".join(perm)
            if word in scrabble_words:
                found_words.add(word)

    return sorted(found_words, key=len, reverse=True)


# Calling actions
if args.Action == "fwrd":
    if not maxl:
        maxl = len(value) + 1
    fwrds = find_valid_words(value, minl, maxl)

    # Print word count
    print(f"Total words found: {len(fwrds)}\nlen | word\n")

    # Print words with their lengths
    for word in fwrds:
        print(len(word), word)

if args.Action == "iswrd":
    print(is_scrabble_word(value))

if args.Action == "letters":
    for i in range(len(value)):
        print(value[i : i + 1])
