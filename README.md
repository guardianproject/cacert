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

The resulting keystore will be in `stores/debiancacerts.bks` ready to be
imported into an Android project.

Add it as a raw resource to your project, then use something like the following
to load it:

```java
    mTrustStore = KeyStore.getInstance("BKS");
    in = mContext.getResources().openRawResource(R.raw.cacerts);
    mTrustStore.load(in, new String("changeit").toCharArray());
```

## Relevant Reading

* [DigiNotar Debacle](https://blog.torproject.org/blog/diginotar-debacle-and-what-you-should-do-about-it)
* [Your app shouldn't suffer SSL's problems](http://thoughtcrime.org/blog/authenticity-is-broken-in-ssl-but-your-app-ha/)
* [Unifying Key Store Access in ICS ](http://android-developers.blogspot.com/2012/03/unifying-key-store-access-in-ics.html)
* [ICS Trust Store Implementation](http://nelenkov.blogspot.com/2011/12/ics-trust-store-implementation.html)

## Credits

We would like to ack [Open WhisperSystems](http://whispersystems.org/) as an inspiration for this, as they were
able to push out a small patch through their WhisperCore update tool in order
to modify the keystore to remove DigiNotar.

