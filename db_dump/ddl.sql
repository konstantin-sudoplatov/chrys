--
-- PostgreSQL database dump
--

-- Dumped from database version 10.4 (Ubuntu 10.4-2.pgdg14.04+1)
-- Dumped by pg_dump version 10.4 (Ubuntu 10.4-2.pgdg14.04+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: concepts; Type: TABLE; Schema: public; Owner: chris
--

CREATE TABLE public.concepts (
    cid integer NOT NULL,
    ver smallint NOT NULL,
    clid smallint NOT NULL,
    shallow bytea NOT NULL,
    deep bytea
);


ALTER TABLE public.concepts OWNER TO chris;

--
-- Name: params; Type: TABLE; Schema: public; Owner: chris
--

CREATE TABLE public.params (
    name character varying NOT NULL,
    value character varying,
    description character varying
);


ALTER TABLE public.params OWNER TO chris;

--
-- Name: concepts concepts_pkey; Type: CONSTRAINT; Schema: public; Owner: chris
--

ALTER TABLE ONLY public.concepts
    ADD CONSTRAINT concepts_pkey PRIMARY KEY (cid, ver);


--
-- Name: params params_pkey; Type: CONSTRAINT; Schema: public; Owner: chris
--

ALTER TABLE ONLY public.params
    ADD CONSTRAINT params_pkey PRIMARY KEY (name);


--
-- Name: concepts_ver_idx; Type: INDEX; Schema: public; Owner: chris
--

CREATE INDEX concepts_ver_idx ON public.concepts USING btree (ver);


--
-- PostgreSQL database dump complete
--

