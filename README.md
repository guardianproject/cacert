# Guardian Project CA Bundle for Android

In response to growing concerns about the less-than trustworthy state of the
global Certificate Authority ecosystem, we have decided to began curating our
own CACert keystore for use on Android devices.

This certificate bundle contains all the CAs from the [Mozilla CA Certificate
Store](https://www.mozilla.org/projects/security/certs/) as obtained through
[Debian's ca-certificates
package](http://packages.qa.debian.org/c/ca-certificates.html).

TODO: How to use the pinned certificate store?

### Projects using this cacert

* [NetCipher](https://github.com/guardianproject/onionkit) - strong TLS verification and proxy library for Android

## Usage

We rely on Debian's tool to parse the Mozilla trust database and output PEM
encoded certificates, which we then combine into a keystore ready for inclusion
in Android.

```bash
    git submodule update --init --recursive
    make
```

## Relevant Reading

More on the "DigiNotar Debacle" here:
https://blog.torproject.org/blog/diginotar-debacle-and-what-you-should-do-about-it


In addition, we expect to develop a simple utility for rooted Android devices,
which can download the latest version, and update the CACerts.bks when updates
are made. We also want to reach out to developers of browser applications or
other HTTPS or SSL Socket enabled apps for Android, to use this CACert file,
embedded in their app, instead of the system default file. We are working to do
this in our own apps such as Gibberbot and Orweb today.


## Credits

We would like to ack [Open WhisperSystems](http://whispersystems.org/) as an inspiration for this, as they were
able to push out a small patch through their WhisperCore update tool in order
to modify the keystore to remove DigiNotar.
