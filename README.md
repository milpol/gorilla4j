# What is all about
It is all about storing data in a efficient way. 

Stop! *two* things. First: it is not about any data, but a very special kind: time series. 
Sounds scary but all in all it is just a value (numerical) in time (epoch). 
Second: *but they said that storage is cheap!* Well, so the bubble gum, it is just a buck. Million packs do the million bucks though.
Also, what *they* don't say that we store enormous load of data which we write once and read ~~once~~ never.

## Give me the numbers
As mentioned, we are considering here a time series data (value in time). Let's say we want to store stock price valuation of single company, single day, sampled every 10 second.
8 hours gives 2880 samples, sample is a time (Java long, 8 bytes) and a value (Java double, 8 bytes). Math is simple:

`8 * 60 * 6 * 16 = 46080B = 45KB` 

Phew. That's nothing you'll say. Sure, the bubble gum is just a buck, blah, blah...
How about Gorilla format, can it do any better?

From ad-hoc test:

`~8465B ~= 8,3KB`

(We could compare that to JSON format... but it would not make any sense.)

Just to be clear: we are talking about exact same data, no rounding or data losses, but...

Well, in wise algorithms there is almost always *but*, the one here is how the data is distributed.

# But how?
All answers and technical guts can be found in great paper from the Facebook engineers [Gorilla: A Fast, Scalable, In-Memory Time Series Database](http://www.vldb.org/pvldb/vol8/p1816-teller.pdf)

# Usage
TBD

# Other Java implementation?
Please check excellent [Michael Burman](https://github.com/burmanm) implementation: [gorilla-tsc](https://github.com/burmanm/gorilla-tsc). 

