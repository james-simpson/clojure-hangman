(ns hangman.core
  (require [clojure.string :as str]))

;;;
; Game logic
;;;
(def no-of-guesses 9)

(defn has-been-guessed
  [guessed char]
  (or (contains? guessed char) (= char \space)))

(defn answer-guessed?
  [answer guessed]
  (every? (partial has-been-guessed guessed) answer))

(defn incorrect-guesses
  [answer guessed]
  (remove (partial contains? (set answer)) guessed))

(defn guesses-remaining
  [answer guessed]
  (- no-of-guesses
     (count (incorrect-guesses answer guessed))))

(defn guess-outcome
  [answer guessed]
  (if (answer-guessed? answer guessed)
    :guesser-wins
    (if (= (guesses-remaining answer guessed) 0)
      :setter-wins)))

;;;
; Displaying game state
;;;
(defn char->placeholder
  [guessed char]
  (if (has-been-guessed guessed char)
    char
    "_"))

(defn answer->placeholders
  "Display the answer with only guessed letters revealed e.g. _ o n k _ y"
  [answer guessed]
  (str/join " "
      (map (partial char->placeholder guessed) answer)))

(defn game-state->string
  "Formats a string that represents the current game state"
  [answer guessed]
  (str "\n" (answer->placeholders answer guessed) "\n\n"
       "Already guessed: " (clojure.string/join ", " (sort guessed)) "\n\n"
       (guesses-remaining answer guessed) " guesses remaining\n"))

(defn guesser-wins
  "Formats the string to output if the guesser wins"
  [answer guessed]
  (str "\n" (answer->placeholders answer guessed) "\n\n"
       "Guesser wins!"))

(defn setter-wins
  "Formats the string to output if the setter wins"
  [answer]
  (str "Setter wins! The answer was " answer "."))

;;;
; User input
;;;
(defn read-password
  "Reads console input without displaying it"
  []
  (String/valueOf (.readPassword (System/console))))

; Helper function for converting a single character string
; to a character e.g. "j" to \j
(def str->char first)

; TODO: validate guess (must be a - z)
(defn prompt-guess
  [answer guessed]
  (println (game-state->string answer guessed))
  (println "Guess a letter:")
  (let [guess (str->char (read-line))
        now-guessed (conj guessed guess)
        outcome (guess-outcome answer now-guessed)]
    (case outcome
      :guesser-wins (println (guesser-wins answer now-guessed))
      :setter-wins (println (setter-wins answer))
      (prompt-guess answer now-guessed))))

(defn play
  "Start a new game"
  []
  (println "Setter: enter the word to guess:")
  (let [answer (read-password)
        guessed #{}]
    (prompt-guess answer guessed)))

(defn -main
  []
  (play))