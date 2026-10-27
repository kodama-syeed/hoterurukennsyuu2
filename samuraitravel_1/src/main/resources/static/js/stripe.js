const stripe = Stripe('pk_test_51UR4XBDxnuFTEhXEZkjc8qr9na87F4giqCDUAB79vUSTLjv52dkEEALU1r7FJqrrCIyZeLFVg8C7OTcva6MPIRWg00TI1qzowB');


const paymentButton = document.querySelector('#paymentButton');





paymentButton.addEventListener('click', () => {

    stripe.redirectToCheckout({

        sessionId: sessionId


    })
});