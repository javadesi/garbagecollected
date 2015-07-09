# Introduction #


  1. Install ELPA, which is a tool to install Emacs extensions. Do so by pasting this into Aquamacs:
```
  (let ((buffer (url-retrieve-synchronously
           "http://tromey.com/elpa/package-install.el")))
  (save-excursion
    (set-buffer buffer)
    (goto-char (point-min))
    (re-search-forward "^$" nil 'move)
    (eval-region (point) (point-max))
    (kill-buffer (current-buffer))))
```

  1. Move your cursor to the end and hit C-x C-e.
  1. Now type "M-x package-list-packages". This opens a list of available packages.
  1. Select swank-clojure by pressing "i" on the line.
  1. Select paredit by pressing "i" on the line.
  1. Press "x" to install all selected packages.
  1. Open ~/.emacs and edit it to look as follows:
```
;;; This was installed by package-install.el.
;;; This provides support for the package system and
;;; interfacing with ELPA, the package archive.
;;; Move this code earlier if you want to reference
;;; packages in your .emacs.
(when
    (load
     (expand-file-name "~/.emacs.d/elpa/package.el"))
  (package-initialize))

;; Customize swank-clojure start-up to reflect possible classpath changes
;; M-x ielm `slime-lisp-implementations RET or see `swank-clojure.el' for more info 
(defadvice slime-read-interactive-args (before add-clojure)
(require 'assoc)
(aput 'slime-lisp-implementations 'clojure
(list (swank-clojure-cmd) :init 'swank-clojure-init)))

(require 'slime)
(require 'paredit)
(require 'clojure-mode)
(require 'swank-clojure)

(eval-after-load "slime"
  '(progn
    ;; "Extra" features (contrib)
    (slime-setup 
     '(slime-repl))
    (setq 
     ;; Use UTF-8 coding
     slime-net-coding-system 'utf-8-unix
     ;; Use fuzzy completion (M-Tab)
     slime-complete-symbol-function 'slime-fuzzy-complete-symbol)
    ;; Use parentheses editting mode paredit
    (defun paredit-mode-enable () (paredit-mode 1))
    (add-hook 'slime-mode-hook 'paredit-mode-enable)
    (add-hook 'slime-repl-mode-hook 'paredit-mode-enable)))

;; By default inputs and results have the same color
;; Customize result color to differentiate them
;; Look for `defface' in `slime-repl.el' if you want to further customize
(custom-set-faces
 '(slime-repl-result-face ((t (:foreground "LightGreen")))))

(eval-after-load "swank-clojure"
  '(progn
    ;; Make REPL more friendly to Clojure (ELPA does not include this?)
    ;; The function is defined in swank-clojure.el but not used?!?
    (add-hook 'slime-repl-mode-hook
      'swank-clojure-slime-repl-modify-syntax t)))
```