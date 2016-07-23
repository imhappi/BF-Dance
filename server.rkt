#lang racket

(require threading) ; threading macros

;; listens on port and sends output to the thread mailbox for 
;;   output-thread.
(define (dostuff port output-thread-spawner)
  (define listener (tcp-listen port))
  ;; spawn a thread per connection
  (let loop ()
    (define-values (in out) (tcp-accept listener))
    (thread (lambda ()
              (define output-thread (output-thread-spawner))
              ;; for a single connection, read continuously until EOF
              (let read-loop ([char (read-char in)])
                (unless (eof-object? char)
                  (thread-send output-thread char)
                  (read-loop (read-char in)))
                (thread-send output-thread 'done)
                (close-input-port in)
                (close-output-port out))))
    (loop)))


(define (spawn-output-thread)
  (thread
    (lambda ()
      (let loop ()
        (match (thread-receive)
               [(? char? char)
                (display char)
                (loop)]
               ['done (eprintf "Done!\n")]
               [(and _ wtf) (eprintf "wtf: ~s\n" wtf)])))))

(dostuff 12346 spawn-output-thread)
