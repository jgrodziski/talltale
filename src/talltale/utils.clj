(ns talltale.utils)

(defmacro create-map
  [& syms]
  (zipmap (map keyword syms) syms))
