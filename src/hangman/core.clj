(ns hangman.core
  (:import (java.io Console)))

;;;
; Game logic
;;;
(defn take-guess [guess guessed]
  (conj guessed guess))

(defn answer-guessed?
  [answer guessed]
  (every?
    #(or (guessed (str %)) (= % \space))
    answer))

(def no-of-guesses 9)

(defn guess-outcome
  [answer guessed]
  (if (answer-guessed? answer guessed)
    :guesser-wins
    (if (> (count guessed) no-of-guesses)
      :setter-wins)))

(defn guesses-remaining
  [guessed]
  (- no-of-guesses (count guessed)))

;;;
; Displaying game state
;;;
(defn char->placeholder
  [char guessed]
  (if (or (contains? guessed (str char)) (= char \space))
    (str char)
    "_"))

(defn answer->placeholders
  [answer guessed]
  (clojure.string/join " "
                       (map #(char->placeholder % guessed) answer)))

(defn game-state->string
  [answer guessed]
  (str "\n" (answer->placeholders answer guessed) "\n\n"
       "Already guessed: " (clojure.string/join ", " (sort guessed)) "\n\n"
       (guesses-remaining guessed) " guesses remaining\n"))

;;;
; User input
;;;
(defn read-password []
  (String/valueOf (.readPassword (System/console))))

; TODO: validate guess (must be a - z)
(defn prompt-guess
  [answer guessed]
  (println (game-state->string answer guessed))
  (println "Guess a letter:")
  (let [guess (read-line)
        now-guessed (take-guess guess guessed)
        outcome (guess-outcome answer now-guessed)]
    (case outcome
      :guesser-wins (println "\n" (answer->placeholders answer now-guessed) "\n\n" "Guesser wins!")
      :setter-wins (println (str "Setter wins! The answer was " answer "."))
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